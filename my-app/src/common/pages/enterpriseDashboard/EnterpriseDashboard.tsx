import { useEffect, useMemo, useState } from 'react';
import { Delete, Edit } from '@mui/icons-material';
import { Alert, Button, CircularProgress, IconButton, Typography } from '@mui/material';
import { Box, Container, margin } from '@mui/system';
import { MaterialReactTable, useMaterialReactTable, type MRT_ColumnDef } from 'material-react-table';
import { ConfirmModal } from '../../components/ConfirmModal';
import { useNotification } from '../../hooks/useNotification';
import api from '../../api';
import { RegisterAdvantageModal } from './RegisterAdvantageModal';
import LeftNavigationMenu from '../../components/teacherLeftNavMenu/LeftNavigationMenu';
import { useNavigate } from 'react-router';

export const EnterpriseDashboard = () => {
    const [advantage, setAdvantage] = useState<any | undefined>(undefined);
    const [data, setData] = useState<any[]>([]);
    const [open, setOpen] = useState(false);
    const [reload, setReload] = useState(true);
    const [id, setId] = useState<number | undefined>(undefined);
    const [openConfirmModal, setOpenConfirmModal] = useState(false);
    const { showNotification } = useNotification();
    const [enterprise, setEnterprise] = useState<any | undefined>(undefined);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | undefined>(undefined);

    const toggleConfirmModal = () => setOpenConfirmModal(!openConfirmModal);
    const toggleModal = () => setOpen(!open);

    const navigate = useNavigate();

    useEffect(() => {
        fetchAdvantages();
    }, [reload]);

    const fetchAdvantages = () => {
        const storedUserId = localStorage.getItem('userId');
        const userId = storedUserId ? JSON.parse(storedUserId).id : null;

        if (!userId) {
            console.error('User ID not found in local storage.');
            return;
        }

        api
            .get(`/advantages/${userId}`)
            .then((response) => {
                setData(response.data);
                setReload(false);
            })
            .catch((error) => console.error(error));
    };


    const deleteAdvantage = () => {
        if (!id) return;

        api
            .delete(`/advantages/${id}`)
            .then((response) => {
                setReload(true);
                showNotification({
                    message: response.data.message,
                    title: response.data.title,
                    type: 'success',
                });
            })
            .catch((error) => console.error(error));
    };

    const columns = useMemo<MRT_ColumnDef<any>[]>(
        () => [
            {
                accessorKey: 'name',
                header: 'Advantage Name',
            },
            {
                accessorKey: 'description',
                header: 'Description',
            },
            {
                accessorKey: 'advantageValue',
                header: 'Value',
                Cell: ({ cell }) => `${cell.getValue<number>()} Moedas`,
            },
            {
                accessorKey: 'urlImage',
                header: 'Image',
                Cell: ({ cell }) => (
                    <img
                        src={cell.getValue<string>()}
                        alt="Advantage"
                        style={{ maxWidth: '50px', maxHeight: '50px', objectFit: 'cover' }}
                    />
                ),
            },
        ],
        []
    );

    const table = useMaterialReactTable({
        columns,
        data,
        enableRowActions: true,
        renderRowActions: ({ row }) => (
            <Box sx={{ display: 'flex', gap: '8px' }}>
                <IconButton
                    onClick={() => {
                        setOpen(true);
                        setAdvantage(row.original);
                    }}
                >
                    <Edit />
                </IconButton>
                <IconButton
                    color="error"
                    onClick={() => {
                        setId(row.original.id);
                        toggleConfirmModal();
                    }}
                >
                    <Delete />
                </IconButton>
            </Box>
        ),
        muiTableContainerProps: { sx: { maxWidth: '100%', height: '60%' } },
    });

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    }

    useEffect(() => {
        setLoading(true);
        const userIdObject = JSON.parse(localStorage.getItem('userId') || '0');
        const id = userIdObject.id;

        api.get(`/companies/${id}`)
            .then((response) => {
                setEnterprise(response.data);
                setLoading(false);
            })
            .catch(() => {
                setError('Failed to load professor');
            });
    }, []);

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <>
            <Container style={{ marginLeft: '282px' }}>
                <header className="grid grid-rows-2 grid-flow-col gap-4 grid-cols-4">
                    <Typography variant="h4" className="col-span-4">
                        Dashboard - Vantagens
                    </Typography>
                    <Box className="row-start-2 col-start-1 col-end-5 flex justify-between items-center">
                        <Button variant="contained" onClick={toggleModal}>
                            Adicionar vantagem
                        </Button>
                    </Box>
                </header>

                <LeftNavigationMenu onLogout={handleLogout} userName={enterprise?.name || 'User'} />

                <Box display="grid" className="my-5">
                    <MaterialReactTable table={table} />
                    <RegisterAdvantageModal
                        open={open}
                        onClose={toggleModal}
                        advantage={advantage}
                        setReload={setReload}
                    />
                    <ConfirmModal
                        openModal={openConfirmModal}
                        title="Confirm Deletion"
                        dialogText="Are you sure you want to delete this advantage?"
                        closeAction={toggleConfirmModal}
                        confirmAction={deleteAdvantage}
                    />
                </Box>
            </Container>
        </>
    );
};
