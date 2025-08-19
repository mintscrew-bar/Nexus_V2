# Cursor AI 개발 규칙 - Nexus 프로젝트

## 🚀 프로젝트 개요
- **프로젝트명**: Nexus (리그오브레전드 내전 플랫폼)
- **기술 스택**: Spring Boot + Next.js + PostgreSQL + Redis
- **AI 도구**: Cursor AI를 활용한 페어 프로그래밍

---

## 📁 프로젝트 구조

```
nexus/
├── backend/                          # Spring Boot 백엔드
│   ├── src/main/java/com/nexus/
│   │   ├── NexusApplication.java    # 메인 애플리케이션
│   │   ├── config/                  # 설정 클래스
│   │   ├── controller/              # REST 컨트롤러
│   │   ├── service/                 # 비즈니스 로직
│   │   ├── repository/              # 데이터 접근 계층
│   │   ├── entity/                  # JPA 엔티티
│   │   ├── dto/                     # 데이터 전송 객체
│   │   └── exception/               # 예외 처리
│   ├── src/main/resources/
│   │   ├── application.yml          # 스프링 설정
│   │   └── db/migration/            # 데이터베이스 마이그레이션
│   └── build.gradle                 # 빌드 설정
├── frontend/                        # Next.js 프론트엔드
│   ├── src/
│   │   ├── pages/                   # Next.js 페이지
│   │   ├── components/              # React 컴포넌트
│   │   ├── hooks/                   # 커스텀 훅
│   │   ├── services/                # API 서비스
│   │   ├── stores/                  # 상태 관리 (Zustand)
│   │   ├── types/                   # TypeScript 타입 정의
│   │   └── utils/                   # 유틸리티 함수
│   ├── public/                      # 정적 파일
│   ├── package.json                 # 패키지 설정
│   └── next.config.js               # Next.js 설정
├── docker-compose.yml               # 개발 환경 설정
└── README.md                        # 프로젝트 문서
```

---

## 🎯 Cursor AI 사용 규칙

### 1. 코드 생성 요청 방법

#### ✅ 올바른 요청 예시
```
"UserService 클래스를 생성해줘. 
- JPA Repository를 사용해서 사용자 CRUD 작업
- 이메일 중복 검사 메서드 포함
- @Transactional 어노테이션 적용
- 예외 처리도 포함해줘"
```

#### ❌ 잘못된 요청 예시
```
"사용자 기능 만들어줘"  (너무 모호함)
"코드 짜줘"           (구체적이지 않음)
```

### 2. 컨텍스트 제공 규칙

#### 파일 생성 시 필수 정보
- 어떤 패키지/디렉토리에 위치할지
- 연관된 다른 클래스/컴포넌트 정보
- 사용할 라이브러리/프레임워크
- 예상되는 입력/출력 데이터 형태

#### 수정 요청 시 필수 정보
- 현재 코드의 문제점
- 원하는 변경 사항
- 영향을 받을 수 있는 다른 코드

---

## 🔧 백엔드 개발 규칙

### 1. 네이밍 컨벤션

```java
// 클래스명: PascalCase
public class UserService { }
public class GameController { }
public class CustomGameRepository { }

// 메서드명: camelCase
public User createUser() { }
public List<Game> findAvailableGames() { }
public boolean checkEmailDuplicate() { }

// 상수: UPPER_SNAKE_CASE
public static final int MAX_GAME_PARTICIPANTS = 10;
public static final String DEFAULT_GAME_MODE = "CLASSIC";

// 변수명: camelCase
private String userName;
private List<GameRoom> gameRooms;
```

### 2. 어노테이션 사용 규칙

```java
// Controller 클래스
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    
    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Valid UserCreateRequestDto request) {
        // 구현
    }
}

// Service 클래스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    @Transactional
    public User createUser(UserCreateRequestDto request) {
        // 구현
    }
}

// Entity 클래스
@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
}
```

### 3. 예외 처리 패턴

```java
// 커스텀 예외 정의
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}

// 전역 예외 처리
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        log.warn("Duplicate email error: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(ErrorResponse.of("DUPLICATE_EMAIL", e.getMessage()));
    }
}
```

### 4. DTO 패턴

```java
// Request DTO
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 2, max = 50, message = "사용자명은 2-50자여야 합니다")
    private String username;
}

// Response DTO
@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
```

---

## ⚛️ 프론트엔드 개발 규칙

### 1. 컴포넌트 구조

```typescript
// React 컴포넌트 기본 구조
import React from 'react';
import { Button, Dialog, DialogTitle, DialogContent } from '@mui/material';

interface Props {
  open: boolean;
  onClose: () => void;
  title: string;
}

export const CustomDialog: React.FC<Props> = ({ open, onClose, title }) => {
  const [loading, setLoading] = useState(false);
  
  const handleSubmit = async () => {
    try {
      setLoading(true);
      // 로직 구현
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        {/* 내용 */}
      </DialogContent>
    </Dialog>
  );
};

export default CustomDialog;
```

### 2. 타입 정의

```typescript
// types/user.ts
export interface User {
  id: number;
  email: string;
  username: string;
  profileImageUrl?: string;
  createdAt: string;
}

export interface CreateUserRequest {
  email: string;
  username: string;
}

export interface UserResponse {
  success: boolean;
  data: User;
  message?: string;
}

// types/game.ts
export interface GameRoom {
  id: number;
  roomCode: string;
  title: string;
  gameMode: GameMode;
  maxParticipants: number;
  currentParticipants: number;
  status: GameStatus;
  hostUser: User;
  createdAt: string;
}

export type GameMode = 'CLASSIC' | 'ARAM' | 'URF';
export type GameStatus = 'WAITING' | 'IN_PROGRESS' | 'COMPLETED';
```

### 3. API 서비스 패턴

```typescript
// services/api.ts
import axios, { AxiosResponse } from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

// 요청 인터셉터
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // 토큰 갱신 로직
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

// services/userService.ts
import api from './api';
import { User, CreateUserRequest, UserResponse } from '../types/user';

export const userService = {
  async createUser(data: CreateUserRequest): Promise<User> {
    const response: AxiosResponse<UserResponse> = await api.post('/users', data);
    return response.data.data;
  },

  async getUserById(id: number): Promise<User> {
    const response: AxiosResponse<UserResponse> = await api.get(`/users/${id}`);
    return response.data.data;
  },
};
```

### 4. 상태 관리 (Zustand)

```typescript
// stores/userStore.ts
import { create } from 'zustand';
import { User } from '../types/user';

interface UserState {
  user: User | null;
  loading: boolean;
  error: string | null;
  
  // Actions
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  user: null,
  loading: false,
  error: null,
  
  setUser: (user) => set({ user }),
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
  clearError: () => set({ error: null }),
}));

// 사용 예시
const LoginForm: React.FC = () => {
  const { user, loading, setUser, setLoading } = useUserStore();
  
  // 컴포넌트 로직
};
```

---

## 🐛 오류 방지 규칙

### 1. 코드 요청 시 체크리스트

**백엔드 코드 요청 전 확인사항**
- [ ] 어떤 패키지에 생성할 클래스인가?
- [ ] 의존성 주입이 필요한 다른 클래스들이 있는가?
- [ ] 데이터베이스 테이블과 연관되는가?
- [ ] 어떤 HTTP 메서드를 사용할 것인가?
- [ ] 예외 처리가 필요한 시나리오는 무엇인가?

**프론트엔드 코드 요청 전 확인사항**
- [ ] 어떤 디렉토리에 위치할 컴포넌트인가?
- [ ] Props로 받을 데이터 타입이 정의되어 있는가?
- [ ] 상태 관리가 필요한가? (로컬 vs 글로벌)
- [ ] API 호출이 필요한가?
- [ ] 어떤 UI 라이브러리 컴포넌트를 사용할 것인가?

### 2. 점진적 개발 원칙

```
❌ 한 번에 전체 기능 요청
"사용자 관리 시스템 전체를 만들어줘"

✅ 단계적으로 요청
1. "User 엔티티 클래스를 만들어줘"
2. "UserRepository 인터페이스를 만들어줘" 
3. "UserService에서 사용자 생성 메서드를 만들어줘"
4. "UserController에서 POST /api/users 엔드포인트를 만들어줘"
```

### 3. 테스트 코드 작성 규칙

```java
// 백엔드 테스트
@SpringBootTest
@Transactional
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    @DisplayName("사용자 생성 시 정상적으로 저장된다")
    void createUser_Success() {
        // given
        UserCreateRequestDto request = UserCreateRequestDto.builder()
            .email("test@example.com")
            .username("testuser")
            .build();
            
        // when
        User result = userService.createUser(request);
        
        // then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
    }
}
```

```typescript
// 프론트엔드 테스트
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from './LoginForm';

describe('LoginForm', () => {
  test('사용자가 로그인 폼을 정상적으로 제출할 수 있다', async () => {
    const user = userEvent.setup();
    const onSubmit = jest.fn();
    
    render(<LoginForm onSubmit={onSubmit} />);
    
    await user.type(screen.getByLabelText(/이메일/i), 'test@example.com');
    await user.type(screen.getByLabelText(/비밀번호/i), 'password123');
    await user.click(screen.getByRole('button', { name: /로그인/i }));
    
    expect(onSubmit).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
  });
});
```

---

## 🚨 일반적인 오류 패턴과 해결법

### 1. Import 오류 해결

```typescript
// ❌ 잘못된 import
import { Button } from 'mui/material';
import React, { useState, useEffect } from 'React';

// ✅ 올바른 import  
import { Button } from '@mui/material';
import React, { useState, useEffect } from 'react';
```

### 2. TypeScript 타입 오류 해결

```typescript
// ❌ 타입 정의 없음
const handleSubmit = (data) => {  // any 타입
  // 로직
};

// ✅ 명시적 타입 정의
interface FormData {
  email: string;
  username: string;
}

const handleSubmit = (data: FormData) => {
  // 로직
};
```

### 3. 환경변수 설정

```yaml
# application.yml (백엔드)
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/nexus}
    username: ${SPRING_DATASOURCE_USERNAME:nexus}
    password: ${SPRING_DATASOURCE_PASSWORD:nexus123}
    
riot:
  api-key: ${RIOT_API_KEY:YOUR_RIOT_API_KEY}
```

```bash
# .env.local (프론트엔드)
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws
```

---

## 📝 Cursor AI 요청 템플릿

### 백엔드 개발 템플릿

```
"{클래스명}를 생성해줘.

위치: src/main/java/com/nexus/{패키지}/
목적: {기능 설명}
의존성: {사용할 다른 클래스들}
어노테이션: {필요한 어노테이션들}
메서드: 
- {메서드명}: {기능 설명}
- {메서드명}: {기능 설명}

예외 처리: {예상되는 예외 상황들}
테스트: {테스트해야 할 시나리오}"
```

### 프론트엔드 개발 템플릿

```
"{컴포넌트명} 컴포넌트를 생성해줘.

위치: src/components/{폴더}/
목적: {컴포넌트 기능 설명}
Props: {받을 props와 타입}
상태: {필요한 state들}
이벤트: {처리할 이벤트들}
API: {호출할 API가 있다면}
UI: {사용할 MUI 컴포넌트들}

스타일링: {특별한 스타일링 요구사항}
반응형: {모바일 대응 필요 여부}"
```

---

## ✅ 최종 체크리스트

### 코드 작성 후 확인사항

**백엔드**
- [ ] 모든 클래스에 적절한 어노테이션이 있는가?
- [ ] 예외 처리가 구현되어 있는가?
- [ ] API 문서화를 위한 Swagger 어노테이션이 있는가?
- [ ] 트랜잭션 처리가 적절한가?
- [ ] 로깅이 구현되어 있는가?

**프론트엔드**
- [ ] TypeScript 타입이 모두 정의되어 있는가?
- [ ] 에러 처리가 구현되어 있는가?
- [ ] 로딩 상태가 관리되고 있는가?
- [ ] 접근성(a11y)을 고려했는가?
- [ ] 반응형 디자인이 적용되어 있는가?

이 규칙들을 따르면 Cursor AI와 함께 오류 없는 안정적인 코드를 생성할 수 있습니다!