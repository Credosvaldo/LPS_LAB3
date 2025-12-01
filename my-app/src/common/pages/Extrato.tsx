import React, { useEffect, useState } from 'react';
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Alert, TextField, TableSortLabel } from '@mui/material';
import api from '../api';
import LeftNavigationMenu from '../components/teacherLeftNavMenu/LeftNavigationMenu';
import { useLocation, useNavigate } from 'react-router';
import { Student } from '../types/student';
import { Professor } from '../types/Professor';
import { PurchaseResponseDTO } from '../types/PurchaseResponseDTO';
import { Donation } from '../types/Donation';
import { DonationResponse } from '../types/DonationResponse';
 
const Extrato: React.FC = () => {
    const [donations, setDonations] = useState<Donation[]>([]);
    const [advantages, setAdvantages] = useState<PurchaseResponseDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>();
    const [searchTerm, setSearchTerm] = useState('');
    const [orderBy, setOrderBy] = useState<'date' | 'donationValue'>('date');
    const [orderDirection, setOrderDirection] = useState<'asc' | 'desc'>('asc');
    const [isStudent, setIsStudent] = useState(false);

    const [user, setUser] = useState<Student | Professor | null>(null);

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const userIdObject = JSON.parse(localStorage.getItem('userId') || '0');
        const id = userIdObject.id;

        const isTeacherExtrato = location.pathname === '/teacher/extrato';
        const userEndpoint = isTeacherExtrato ? `/professors/${id}` : `/students/${id}`;
        const donationsEndpoint = isTeacherExtrato ? `/donations/professor/${id}` : `/donations/student/${id}`;

        setIsStudent(!isTeacherExtrato);

        api.get(userEndpoint)
            .then((response) => setUser(response.data))
            .catch(() => setError('Failed to load user'));

        api.get(donationsEndpoint)
            .then((response) => {
                const dados: DonationResponse[] = response.data;
                const formattedData = dados.map(item => ({
                    ...item,
                    date: new Date(item.date[0], item.date[1] - 1, item.date[2], item.date[3], item.date[4], item.date[5])
                }));
                setDonations(formattedData);
                setLoading(false);
            })
            .catch(() => {
                setError('Failed to load donations');
                setLoading(false);
            });

            if (!isTeacherExtrato) {
                api.get(`/purchases/${id}`)
                    .then((response) => {
                        const formattedAdvantages = response.data.map((advantage: PurchaseResponseDTO) => ({
                            ...advantage,
                            date: new Date(
                                advantage.date[0], // Year
                                advantage.date[1] - 1, // Month (0-indexed)
                                advantage.date[2] // Day
                            ).toLocaleDateString('pt-BR') // Format as dd/mm/yyyy
                        }));
                        setAdvantages(formattedAdvantages);
                    })
                    .catch(() => setError('Failed to load advantages'));
            }            
    }, [location.pathname]);

    const filteredDonations = donations.filter(donation =>
        donation.student.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // const filteredDonations = isStudent
    // ? [
    //       ...donations.filter(donation =>
    //           donation.professor.name.toLowerCase().includes(searchTerm.toLowerCase())
    //       ),
    //       ...advantages.filter(advantage =>
    //           advantage.name.toLowerCase().includes(searchTerm.toLowerCase())
    //       ),
    //   ]
    // : donations.filter(donation =>
    //       donation.student.name.toLowerCase().includes(searchTerm.toLowerCase())
    //   );


    const sortedDonations = [...filteredDonations].sort((a, b) => {
        if (orderBy === 'date') {
            return (new Date(a.date).getTime() - new Date(b.date).getTime()) * (orderDirection === 'asc' ? 1 : -1);
        }
        return (a.donationValue - b.donationValue) * (orderDirection === 'asc' ? 1 : -1);
    });

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Container style={{ marginLeft: '282px' }}>
            <LeftNavigationMenu onLogout={handleLogout} userName={user?.name || 'User'} />
            <Typography variant="h4" gutterBottom>Extrato</Typography>
            <TextField
                label={isStudent ? "Search by Professor or Advantage Name" : "Search by Student Name"}
                variant="outlined"
                fullWidth
                margin="normal"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>{isStudent ? "Professor Name / Advantage" : "Student Name"}</TableCell>
                            {!isStudent && <TableCell>Course Name</TableCell>}
                            <TableCell>Date</TableCell>
                            <TableCell>Amount</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {sortedDonations.map((donation) => (
                            <TableRow key={donation.id}>
                                <TableCell style={{ color: isStudent ? 'darkgreen' : undefined }}>
                                    {isStudent ? donation.professor.name : donation.student.name}
                                </TableCell>
                                {!isStudent && <TableCell>{donation.student.course.name}</TableCell>}
                                <TableCell>{new Date(donation.date).toLocaleDateString()}</TableCell>
                                <TableCell>{donation.donationValue}</TableCell>
                            </TableRow>
                        ))}
                        {isStudent && advantages.map((advantage) => (
                            <TableRow key={advantage.id}>
                                <TableCell style={{ color: 'red' }}>{advantage.name}</TableCell>
                                <TableCell>{advantage.date}</TableCell>
                                <TableCell>{advantage.totalValue}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Container>
    );
};

export default Extrato;
