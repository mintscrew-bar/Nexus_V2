// frontend/src/theme/theme.ts
'use client';
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        mode: 'dark',
        // Discord Blurple + OP.GG Blue 믹스 톤
        primary: {
            main: '#5865F2',
            light: '#7B86F6',
            dark: '#3C45A5',
        },
        secondary: {
            main: '#1E9EFF',
            light: '#4CB2FF',
            dark: '#0F6FB8',
        },
        background: {
            // 배경: 아주 어두운 네이비 톤
            default: '#0E1117',
            paper: '#151A21',
        },
        text: {
            primary: '#E6E8EA',
            secondary: '#AAB2BD',
        },
        divider: 'rgba(255,255,255,0.08)'
    },
    shape: {
        borderRadius: 10,
    },
});

export default theme;