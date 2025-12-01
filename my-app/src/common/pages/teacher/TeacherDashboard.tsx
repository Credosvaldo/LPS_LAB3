// TeacherDashboard.tsx
import React, { useEffect, useState } from 'react';
import { Container, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, CircularProgress, Alert, Box } from '@mui/material';
import api from '../../api'; // Adjust the import to your API setup
import LeftNavigationMenu from '../../components/teacherLeftNavMenu/LeftNavigationMenu';
import DonateModal from '../../components/donateModal/DonateModal';
import { useNavigate } from 'react-router';
import { Address } from '../../types/address';
import { Student } from '../../types/student';
import { Professor } from '../../types/Professor';


const TeacherDashboard: React.FC = () => {
    const [students, setStudents] = useState<Student[]>([]);
    const [professor, setProfessor] = useState<Professor>({} as Professor);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [modalOpen, setModalOpen] = useState(false);
    const [selectedStudentId, setSelectedStudentId] = useState<number | null>(null);

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    }

    useEffect(() => {
        if(modalOpen)
            return
        
        setLoading(true);
        api.get('/students')
            .then((response) => {
                setStudents(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError('Failed to load students');
                setLoading(false);
            });

        const userIdObject = JSON.parse(localStorage.getItem('userId') || '0');
        const id = userIdObject.id;

        api.get(`/professors/${id}`)
            .then((response) => {
                setProfessor(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError('Failed to load professor');
            });
    }, [modalOpen]);

    const handleDonateClick = (studentId: number) => {
        setSelectedStudentId(studentId);
        setModalOpen(true);
    };

    const onDonationSuccess = (data: Professor) => {
        setProfessor(data);
    }

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Container style={{ marginLeft: '282px' }}>
            <LeftNavigationMenu onLogout={handleLogout} userName={professor.name || 'User'} />
            <Typography variant="h4" gutterBottom>Teacher Dashboard</Typography>

            {/* Balance Card */}
            <Card sx={{ mb: 4, border: '1px solid #ccc' }}>
                <CardContent>
                    <Typography variant="h6">Saldo atual</Typography>
                    <Typography variant="h4" align="center" sx={{ fontWeight: 'bold', mt: 2 }}>
                        {professor.balance ? `${professor.balance}` : '0'}
                    </Typography>
                </CardContent>
            </Card>

            {/* Students Table */}
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Balance</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {students.map((student) => (
                            <TableRow key={student.id}>
                                <TableCell>{student.name}</TableCell>
                                <TableCell>{student.balance}</TableCell>
                                <TableCell>
                                    <Button variant="contained" onClick={() => handleDonateClick(student.id)}>
                                        Doar
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            {selectedStudentId !== null && (
                <DonateModal
                    open={modalOpen}
                    onClose={() => setModalOpen(false)}
                    professorId={professor.id}
                    studentId={selectedStudentId}
                    onDonationSuccess={onDonationSuccess}
                />
            )}
        </Container>
    );
};

export default TeacherDashboard;
