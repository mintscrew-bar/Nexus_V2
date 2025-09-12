// frontend/src/components/CreateRoomModal.tsx
'use client';

import React, { useState } from 'react';
import { Modal, Box, Typography, TextField, Button, Slider } from '@mui/material';

interface CreateRoomModalProps {
    open: boolean;
    onClose: () => void;
    onCreate: (title: string, maxParticipants: number) => Promise<void>;
}

const style = {
    position: 'absolute' as 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

const CreateRoomModal: React.FC<CreateRoomModalProps> = ({ open, onClose, onCreate }) => {
    const [title, setTitle] = useState('');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [isCreating, setIsCreating] = useState(false);

    const handleSubmit = async () => {
        setIsCreating(true);
        try {
            await onCreate(title, maxParticipants);
            onClose(); // 성공 시 모달 닫기
        } catch (error) {
            console.error("Failed to create room:", error);
            // 사용자에게 에러 알림을 보여주는 로직 추가 가능
        } finally {
            setIsCreating(false);
        }
    };

    return (
        <Modal open={open} onClose={onClose}>
            <Box sx={style}>
                <Typography variant="h6" component="h2">
                    새로운 방 만들기
                </Typography>
                <TextField
                    autoFocus
                    margin="dense"
                    label="방 제목"
                    type="text"
                    fullWidth
                    variant="standard"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
                <Typography gutterBottom sx={{ mt: 2 }}>
                    최대 참가 인원: {maxParticipants}
                </Typography>
                <Slider
                    aria-label="Max Participants"
                    value={maxParticipants}
                    onChange={(_, newValue) => setMaxParticipants(newValue as number)}
                    valueLabelDisplay="auto"
                    step={10}
                    marks
                    min={10}
                    max={50}
                />
                <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                    <Button onClick={onClose}>취소</Button>
                    <Button onClick={handleSubmit} variant="contained" disabled={isCreating || !title}>
                        {isCreating ? '생성 중...' : '만들기'}
                    </Button>
                </Box>
            </Box>
        </Modal>
    );
};

export default CreateRoomModal;