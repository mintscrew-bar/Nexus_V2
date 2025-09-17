'use client';

import React from 'react';
import { Button, Stack, Box } from '@mui/material';
import { oauthRedirect } from '@/services/auth';

const OAuthButtons = () => {
    return (
        <Stack spacing={2}>
            <Button
                variant="outlined"
                onClick={() => oauthRedirect('google')}
                size="large"
                fullWidth
                startIcon={
                    <Box
                        component="img"
                        src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIyLjU2IDEyLjI1YzAtLjc4LS4wNy0xLjUzLS4yLTIuMjVIMTJWMTRoNi4xOUM3Ljc5IDE4Ljc0IDIuNjYgMTguNzQgMC44IDE2Ljk5IDAgMTUuODkgMCAxNC44NCAwIDE0SDBjIDAtNS41MiA0LjQ4LTEwIDEwLTEwIDIuNzUgMCA1LjI0LjcyIDcuMDQgMS44OWwtNS40MyA1LjQzYy0uNzQtLjQ2LTEuNi0uNzQtMi42MS0uNzQtMi4yNiAwLTQuNDcgMS42NC01IDMuNzlINi4wOGMtLjA4LS41MS0uMDgtMS4yOC0uMDgtMS4zNyAwLTEuOTYuNTctMy41NyAxLjQ1LTQuOTFDNy44IDQuNzIgOS43IDMuNzQgMTIgMy43NGMxLjcxIDAgMy4zOS40MSA0Ljc5IDEuMjFMMjEuNSAzbDEuMDYgOS4yNXoiIGZpbGw9IiM0Mjg1RjQiLz4KPHN0ZWggZD0iTTIyLjU2IDEyLjI1YzAtLjc4LS4wNy0xLjUzLS4yLTIuMjVIMTJWMTRoNi4xOUMxNy4yNSAxNi44MSAxNS4yNSAxOS4yNyAxMiAxOS4yN2MtMS45MyAwLTMuNjMtLjc0LTUuMTItMS43OGwtMi4yNSAyLjI1YzEuOTMgMS42IDQuMzcgMi41IDcuMzcgMi41IDMuNDggMCA2LjQzLTEuMTQgOC41Ny0zLjA5IDIuMTMtMS45NCAzLjQ0LTQuNzggMy40NC04LjE1eiIgZmlsbD0iIzM0QTg1MyIvPgo8L3N2Zz4="
                        alt="Google"
                        width={20}
                        height={20}
                    />
                }
                sx={{
                    color: '#4285F4',
                    borderColor: '#4285F4',
                    '&:hover': {
                        borderColor: '#3367D6',
                        backgroundColor: 'rgba(66, 133, 244, 0.04)',
                    },
                }}
            >
                Google로 계속하기
            </Button>

            <Button
                variant="outlined"
                onClick={() => oauthRedirect('discord')}
                size="large"
                fullWidth
                startIcon={
                    <Box
                        component="img"
                        src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTIwLjMxNyA0LjM2OTgyYy0uNzIzLS4zMjMtMS40ODQtLjU1NC0yLjI2OS0uNzE1LS4yNTMuNTMyLS40NTYgMS4wMDEtLjY0MiAxLjU5NS0uODM4LS4xMjMtMS42NzEtLjE4OC0yLjUwNC0uMTg4LS44MzIgMC0xLjY2NS4wNjUtMi41MDMuMTg4LS4xODctLjU5NC0uMzktMS4wNjMtLjY0My0xLjU5NS0uNzg1LjE2MS0xLjU0Ny4zOTItMi4yNy43MTVDNy42NjkgNy44IDYuMzQ4IDExLjAwNSA2LjgwMyAxNC44OTVjLjg5OC42Ni0xLjc2OCAxLjMwNi0yLjc3IDEuNjc4LjIyOC0uMzA3LjQzLS42My42MDYtLjk2OS4zMi0uNjI1LjU2MS0xLjI5My43NDMtMS45OTgtLjI1Ni0uMTEzLS41MDMtLjI1My0uNzM4LS40MjIuNjEzLS40NDMgMS4yMTQtLjkwMiAxLjc5MS0xLjQwNCA2LjQ2NC0yLjg4NyAxMy41MTktMi44ODcgMTkuOTgzIDBjLjU3Ni41MDIgMS4xNzguOTYxIDEuNzkxIDEuNDA0LS4yMzUuMTY5LS40ODIuMzA5LS43MzguNDIyLjE4Mi43MDUuNDIzIDEuMzczLjc0MyAxLjk5OGMuMTc2LjM0LjM3OC42NjIuNjA2Ljk2OS0xLjAwMi0uMzcyLTEuODcyLTEuMDE4LTIuNzctMS42NzguNDU1LTMuODktLjY2NS03LjA5NS00LjE5Ni0xMC41MjUzOFpNOS42ODMgMTcuMDYzYy0xLjM5IDAtMi41MDUtMS4yNi0yLjUwNS0yLjc3NCAwLTEuNTE1IDEuMDkzLTIuNzczIDIuNTA1LTIuNzczIDEuNDA3IDAgMi41MjYgMS4yNjQgMi41MDUgMi43NzMtLjAwNyAxLjUxNC0xLjEyNyAyLjc3NC0yLjUwNSAyLjc3NFpNMTUuNjE3IDE3LjA2M2MtMS4zOSAwLTIuNTA1LTEuMjYtMi41MDUtMi43NzQgMC0xLjUxNSAxLjA5My0yLjc3MyAyLjUwNS0yLjc3MyAxLjQwNyAwIDIuNTI2IDEuMjY0IDIuNTA1IDIuNzczLS4wMDcgMS41MTQtMS4xMjcgMi43NzQtMi41MDUgMi43NzRaIiBmaWxsPSIjNTg2NUY2Ii8+Cjwvc3ZnPg=="
                        alt="Discord"
                        width={20}
                        height={20}
                    />
                }
                sx={{
                    color: '#5865F6',
                    borderColor: '#5865F6',
                    '&:hover': {
                        borderColor: '#4752C4',
                        backgroundColor: 'rgba(88, 101, 246, 0.04)',
                    },
                }}
            >
                Discord로 계속하기
            </Button>
        </Stack>
    );
};

export default OAuthButtons;



