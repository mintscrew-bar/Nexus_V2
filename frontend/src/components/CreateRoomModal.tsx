// frontend/src/components/CreateRoomModal.tsx
'use client';

import React, { useMemo, useState } from 'react';
import { Modal, Box, Typography, TextField, Button, Slider, Alert } from '@mui/material';

interface CreateRoomModalProps {
    open: boolean;
    onClose: () => void;
    onCreate: (title: string, maxParticipants: number) => Promise<void>;
}

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    backgroundColor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

const CreateRoomModal: React.FC<CreateRoomModalProps> = ({ open, onClose, onCreate }) => {
    const [title, setTitle] = useState('');
    const [maxParticipants, setMaxParticipants] = useState(10);
    const [isCreating, setIsCreating] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const titleError = useMemo(() => {
        if (!title) return '방 제목은 필수입니다.';
        if (title.length < 2) return '방 제목은 2자 이상이어야 합니다.';
        if (title.length > 50) return '방 제목은 50자 이하여야 합니다.';
        return null;
    }, [title]);

    const maxParticipantsError = useMemo(() => {
        if (maxParticipants < 10) return '최소 인원은 10명입니다.';
        if (maxParticipants > 50) return '최대 인원은 50명입니다.';
        if (maxParticipants % 5 !== 0) return '인원은 5명 단위여야 합니다.';
        return null;
    }, [maxParticipants]);

    const handleSubmit = async () => {
        setError(null);
        setIsCreating(true);
        try {
            if (titleError || maxParticipantsError) {
                throw new Error(titleError || maxParticipantsError || '유효하지 않은 입력입니다.');
            }
            await onCreate(title.trim(), maxParticipants);
            onClose(); // 성공 시 모달 닫기
        } catch (error) {
            const message = error instanceof Error ? error.message : '방 생성에 실패했습니다.';
            setError(message);
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
                {error && (
                    <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>
                )}
                <TextField
                    autoFocus
                    margin="dense"
                    label="방 제목"
                    type="text"
                    fullWidth
                    variant="standard"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    error={Boolean(titleError)}
                    helperText={titleError || ' '}
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
                {maxParticipantsError && (
                    <Typography variant="caption" color="error">{maxParticipantsError}</Typography>
                )}
                <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                    <Button onClick={onClose}>취소</Button>
                    <Button onClick={handleSubmit} variant="contained" disabled={isCreating || Boolean(titleError) || Boolean(maxParticipantsError)}>
                        {isCreating ? '생성 중...' : '만들기'}
                    </Button>
                </Box>
            </Box>
        </Modal>
    );
};

export default CreateRoomModal;