import React, { useEffect, useState } from 'react';
import { Container, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, CircularProgress, Alert, Box } from '@mui/material';
import api from '../../api';
import LeftNavigationMenu from '../../components/teacherLeftNavMenu/LeftNavigationMenu';
import DonateModal from '../../components/donateModal/DonateModal';
import { useNavigate } from 'react-router';
import { Student } from '../../types/student';
import { Advantage } from '../../types/Advantage';
import TradeAdvantageModal from './TradeAdvantageModal';


const StudentDashboard: React.FC = () => {
    const [student, setStudent] = useState<Student>({} as Student);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [advantages, setAdvantages] = useState<Advantage[]>([]);

    const [modalOpen, setModalOpen] = useState(false);
    const [selectedAdvantage, setSelectedAdvantage] = useState<Advantage>({} as Advantage);

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    }

    useEffect(() => {
        if(modalOpen)
            return;

        setLoading(true);
        api.get('/advantages')
            .then((response) => {
                setAdvantages(response.data);
            })
            .catch(() => {
                setError('Failed to load advantages');
            });

        const userIdObject = JSON.parse(localStorage.getItem('userId') || '0');
        const id = userIdObject.id;

        api.get(`/students/${id}`)
            .then((response) => {
                setStudent(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError('Failed to load student');
            });
    }, [modalOpen]);

    const openTradeModal = (advantage: Advantage) => {
        setSelectedAdvantage(advantage);
        setModalOpen(true);
    };

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Container style={{ marginLeft: '282px' }}>
            <LeftNavigationMenu onLogout={handleLogout} userName={student.name || 'User'} />
            <Typography variant="h4" gutterBottom>Student Dashboard</Typography>

            <Card sx={{ mb: 4, border: '1px solid #ccc' }}>
                <CardContent>
                    <Typography variant="h6">Saldo atual</Typography>
                    <Typography variant="h4" align="center" sx={{ fontWeight: 'bold', mt: 2 }}>
                        {student.balance ? `${student.balance}` : '0'}
                    </Typography>
                </CardContent>
            </Card>
            <Typography variant="h6" gutterBottom>Tabela de vantagens</Typography>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Image</TableCell>
                            <TableCell>Name</TableCell>
                            <TableCell>Description</TableCell>
                            <TableCell>Price</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {advantages.map((advantage) => (
                            <TableRow key={advantage.id}>
                                <TableCell>
                                    <img src={advantage.urlImage} alt={advantage.name} style={{ width: '50px', height: '50px' }} />
                                </TableCell>
                                <TableCell>{advantage.name}</TableCell>
                                <TableCell>{advantage.description}</TableCell>
                                <TableCell>{advantage.advantageValue}</TableCell>
                                <TableCell>
                                    <Button variant="contained" onClick={() => openTradeModal(advantage)}>
                                        Trocar
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            {modalOpen && (
                <TradeAdvantageModal
                    advantage={selectedAdvantage} 
                    student={student}
                    setStudent={setStudent}
                    onClose={() => setModalOpen(false)}
                />
            )}
        </Container>
    );
};

export default StudentDashboard