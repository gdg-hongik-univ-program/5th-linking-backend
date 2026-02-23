# 🔗 LINKING (링킹) - 5th-linking-backend

![Title](./docs/images/Poster.png)

> **저장된 링크를 효율적으로 관리하고 체계화하는 서비스, LINKING의 백엔드 리포지토리입니다.** <br>
> 여기저기 흩어진 링크들을 폴더와 태그로 정리하고, 링크 간의 관계를 매핑하여 나만의 지식 저장소를 구축합니다.

<br>

## ✨ Core Features

* 👤 **회원 관리 (User Management)**
  * 사용자 가입, 로그인 및 인증/인가 처리
* 🔗 **링크 아이템 관리 (Item CRUD)**
  * 아티클, 영상 등 다양한 URL 링크의 생성, 조회, 수정, 삭제
* 📁 **폴더 구조화 (Folder Structure)**
  * 저장된 링크들을 계층적 폴더 트리로 분류 및 관리
* 🏷️ **태그 기능 (Tagging)**
  * 링크에 커스텀 태그를 부여하여 다차원적인 검색 및 필터링 지원
* 🕸️ **링크 관계성 (Link Relationships)**
  * 연관된 링크 간의 관계(Relationship)를 매핑하여 꼬리를 무는 정보 탐색 제공
* 📅 **캘린더 및 일정 관리 (Calendar Integration)**
  * 저장된 링크를 특정 날짜나 일정과 연동하여 관리하는 기능 제공
* 🔔 **알림 시스템 (Notification)**
  * 사용자 맞춤형 알림 발송 및 알림 이력 관리
* 🔍 **통합 검색 (Advanced Search)**
  * 키워드, 태그, 폴더 등 다양한 조건을 활용한 링크 통합 검색 지원
<br>

## 📂 Project Architecture

**도메인 중심(Domain-Driven) 아키텍처**를 채택하여, 각 기능별로 응집도를 높이고 모듈 간의 결합도를 낮추었습니다.

```text
📦 src/main/java/com/linking
 ┣ 📂 global              # 공통 설정 및 전역 처리
 ┃ ┣ 📂 aop               # 관점 지향 프로그래밍 ( 권한 체크 )
 ┃ ┣ 📂 config            # WebConfig 
 ┃ ┣ 📂 exception         # 전역 예외 처리 로직 (GlobalExceptionHandler)
 ┃ ┗ 📂 utils             # 공통 유틸리티 클래스 (암호화 , 세션 로그인)
 ┃
 ┣ 📂 domain              # 비즈니스 로직을 담은 핵심 도메인 영역
 ┃ ┣ 📂 user              # 회원 및 인증 관리
 ┃ ┣ 📂 link              # 링크 아이템 및 관계(자기 참조) 관리
 ┃ ┣ 📂 folder            # 폴더 계층 구조화
 ┃ ┣ 📂 tag               # 태그 관리 및 N:M 매핑
 ┃ ┣ 📂 calendar          # 캘린더 및 일정 연동
 ┃ ┣ 📂 notification      # 사용자 알림 발송 및 이력 관리
 ┃ ┗ 📂 search            # 링크, 태그 등 통합 검색 기능
 ┃
 ┗ 📜 LinkingApplication.java  # 애플리케이션 실행 
 ```
<br>

## 🛠️ Tech Stack

**Language & Framework**
<br>

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)

**Database & ORM**

![MySQL](https://img.shields.io/badge/mysql-%234479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-gray?style=for-the-badge) 
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

**API Docs & Others**

![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

**CI/CD & Infrastructure**

![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazonaws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white)

<br>


## 🚀 Getting Started

프로젝트를 로컬 환경에서 셋업하고 실행하기 위한 안내입니다.

### Prerequisites
원활한 실행을 위해 아래 도구들이 설치되어 있어야 합니다.
* **Java 17** 이상
* **MySQL 8.0** 이상

### Installation & Run

**1. 리포지토리 클론 (Clone the repository)**
```bash
git clone https://github.com/gdg-hongik-univ-program/5th-linking-backend.git
cd 5th-linking-backend
```

**2. 환경 변수 및 데이터베이스 설정 (Configuration)**

* 로컬 MySQL에 `linking` 이라는 이름의 데이터베이스를 생성합니다.
* `src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 본인의 환경에 맞게 수정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/linking?useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password # 본인의 MySQL 비밀번호로 변경
```

**3. 프로젝트 빌드 및 실행 (Build and Run)**

* Gradle Wrapper를 사용하여 프로젝트를 빌드하고 실행합니다.

```Bash
# Mac / Linux
./gradlew bootRun

# Windows
gradlew bootRun
```

**4. API 명세서 확인 (API Documentation)**

* 서버가 정상적으로 실행되면 (기본 포트 8080), 아래 링크에서 Swagger UI를 통해 API 명세서를 확인하고 테스트할 수 있습니다.

* 👉 Swagger UI: `http://localhost:8080/swagger-ui/index.html`