'use client';

import React from 'react';
import { Box, Typography, Button } from '@mui/material';

interface EmptyStateProps {
    title: string;
    description?: string;
    actionLabel?: string;
    onAction?: () => void;
}

const EmptyState: React.FC<EmptyStateProps> = ({ title, description, actionLabel, onAction }) => {
    return (
        <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" py={8} textAlign="center">
            <Typography variant="h6" gutterBottom>
                {title}
            </Typography>
            {description && (
                <Typography variant="body2" color="text.secondary" paragraph>
                    {description}
                </Typography>
            )}
            {actionLabel && onAction && (
                <Button variant="contained" onClick={onAction} sx={{ mt: 1 }}>
                    {actionLabel}
                </Button>
            )}
        </Box>
    );
};

export default EmptyState;



