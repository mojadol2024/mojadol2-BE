# 면접의 정석

## 🖥️ 프로젝트 소개
면접의 정석은 사용자가 AI 면접 연습을 할 수 있는 웹 기반 서비스다.
면접 연습 과정에서 AI가 질문을 생성하고, 사용자의 답변을 평가하며,
실제 면접 환경과 유사한 경험을 제공한다.

사용자는 자신이 작성한 자소서를 업로드하면,
서비스가 자동으로 질문을 생성하고,
이를 통해 연습할 질문 리스트를 제공한다.
연습 후, 답변을 분석하고 피드백을 제공하여
사용자가 면접 준비를 체계적으로 할 수 있도록 지원한다.

<br>

## 🕰️ 개발 기간
* 25.03.07일 - 현재

### 🧑‍🤝‍🧑 멤버 구성
- 고건민
- 김서나
- 김영언
- 변경태
- 이우림

## ⚙️ 개발환경 
- **Language** : Java 17
- **Security** : SpringSecurity 3.1.3, Jwt 0.11.2
- **IDE** : IntelliJ
- **Framwork** : Spring boot 3.1.3
- **Database** : Mysql 8.0, Redis
- **ORM** : JPA
- **CI/CD** : Docker, GitHub Actions
- **DevOps/Monitoring** : Actuator, Prometheus, Grafana, Kafka, Nginx, GithubActions
- **Etc** : Lombok, Jsch, Pdfbox

## 📌 주요기능
- 면접 결과 PDF로 반환
- 자기소개서 맞춤법 검사
- AI를 이용한 자기소개서 분석 후 질문 리스트 생성
- AI를 이용한 면접 평가
