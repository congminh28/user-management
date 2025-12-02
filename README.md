# User Management System - Spring Boot Application

Hệ thống quản lý người dùng được xây dựng bằng Spring Boot 3.2.0 với đầy đủ các chức năng CRUD, Authentication, Import/Export CSV và REST API.

## 📋 Mục lục

- [Tính năng](#tính-năng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc project](#cấu-trúc-project)
- [Cài đặt và chạy project](#cài-đặt-và-chạy-project)
- [Cấu hình Database](#cấu-hình-database)
- [URLs chính](#urls-chính)
- [REST API Endpoints](#rest-api-endpoints)
- [Tài khoản mặc định](#tài-khoản-mặc-định)
- [Import/Export CSV](#importexport-csv)

## ✨ Tính năng

### Chức năng bắt buộc

- ✅ **CRUD User**
  - Create: Thêm người dùng mới với validation (name, email unique, password)
  - Read: Hiển thị danh sách user kèm phân trang (Pagination)
  - Update: Cập nhật thông tin người dùng
  - Delete: Xóa người dùng
  - Search: Tìm kiếm user theo tên hoặc email

- ✅ **Form Validation**
  - Sử dụng Spring Validation API
  - Validation cho name, email, password
  - Kiểm tra email unique

- ✅ **Responsive UI**
  - Sử dụng Bootstrap 5
  - Giao diện hiện đại, đẹp mắt
  - Responsive trên mọi thiết bị

- ✅ **Authentication**
  - Login với Spring Security 6
  - Logout
  - Bảo vệ các trang quản lý (chỉ vào được khi đã login)

- ✅ **Import/Export CSV**
  - Import danh sách user từ file .csv
  - Export danh sách user ra file .csv
  - Sử dụng OpenCSV library

- ✅ **REST API**
  - API CRUD + Search để mobile app có thể sử dụng
  - Trả về JSON
  - Chuẩn RESTful

- ✅ **Thymeleaf Templates**
  - login.html - Trang đăng nhập
  - users.html - Danh sách user với pagination và search
  - user_form.html - Form thêm/sửa user

## 🛠 Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security 6**
- **Thymeleaf** (Template Engine)
- **MySQL** (Database chính)
- **H2 Database** (Database cho testing)
- **Bootstrap 5** (UI Framework)
- **Validation API** (Jakarta Validation)
- **OpenCSV 5.9** (CSV Import/Export)

## 📁 Cấu trúc project

```
user-management/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── usermanagement/
│   │   │           ├── UserManagementApplication.java
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── WebController.java
│   │   │           │   └── RestApiController.java
│   │   │           ├── entity/
│   │   │           │   └── User.java
│   │   │           ├── repository/
│   │   │           │   └── UserRepository.java
│   │   │           └── service/
│   │   │               ├── UserService.java
│   │   │               └── impl/
│   │   │                   └── UserServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-h2.properties
│   │       └── templates/
│   │           ├── login.html
│   │           ├── users.html
│   │           └── user_form.html
│   └── test/
└── target/
```

## 🚀 Cài đặt và chạy project

### Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0+ (hoặc sử dụng H2 cho testing)
- IDE: IntelliJ IDEA, Eclipse, hoặc VS Code

### Bước 1: Clone hoặc tải project

```bash
cd C:\Users\tranc\user-management
```

### Bước 2: Cấu hình Database

#### Option 1: Sử dụng MySQL (Khuyến nghị cho production)

1. Tạo database MySQL:
```sql
CREATE DATABASE user_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Cập nhật `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

#### Option 2: Sử dụng H2 Database (Cho testing/development)

1. Chạy với profile H2:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Hoặc thêm vào `application.properties`:
```properties
spring.profiles.active=h2
```

### Bước 3: Build project

```bash
mvn clean install
```

### Bước 4: Chạy application

```bash
mvn spring-boot:run
```

Hoặc chạy từ IDE:
- Mở `UserManagementApplication.java`
- Click Run hoặc Debug

### Bước 5: Truy cập ứng dụng

Mở trình duyệt và truy cập:
- **Web Interface**: http://localhost:8080/login
- **H2 Console** (nếu dùng H2): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:userdb`
  - Username: `sa`
  - Password: (để trống)

## 🔗 URLs chính

### Web Interface

| URL | Mô tả |
|-----|-------|
| `/login` | Trang đăng nhập |
| `/users` | Danh sách người dùng (yêu cầu đăng nhập) |
| `/users/new` | Form thêm người dùng mới |
| `/users/edit/{id}` | Form sửa thông tin người dùng |
| `/users/delete/{id}` | Xóa người dùng |
| `/users/import` | Import CSV (POST) |
| `/users/export` | Export CSV (GET) |
| `/logout` | Đăng xuất |

### REST API

| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/api/users` | Lấy danh sách user (có pagination) |
| GET | `/api/users/{id}` | Lấy thông tin user theo ID |
| POST | `/api/users` | Tạo user mới |
| PUT | `/api/users/{id}` | Cập nhật user |
| DELETE | `/api/users/{id}` | Xóa user |
| GET | `/api/users/search?keyword=...` | Tìm kiếm user |

## 🔐 JWT Authentication cho REST API

### Đăng nhập để lấy JWT Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "Administrator",
    "email": "admin@example.com"
  }
}
```

### Đăng ký tài khoản mới

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "New User",
  "email": "newuser@example.com",
  "password": "password123"
}
```

### Lấy thông tin user hiện tại

```http
GET /api/auth/me
Authorization: Bearer YOUR_JWT_TOKEN
```

### Sử dụng JWT Token

Tất cả các API endpoints (trừ `/api/auth/login` và `/api/auth/register`) yêu cầu JWT token trong header:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

**Ví dụ:**
```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
     http://localhost:8080/api/users
```

## 📡 REST API Endpoints

**Lưu ý:** Tất cả các endpoints dưới đây yêu cầu JWT token trong header `Authorization: Bearer <token>`

### 1. Lấy danh sách users (có pagination)

```http
GET /api/users?page=0&size=10&keyword=john
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "users": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ],
  "currentPage": 0,
  "totalPages": 1,
  "totalItems": 1,
  "pageSize": 10
}
```

### 2. Lấy user theo ID

```http
GET /api/users/1
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### 3. Tạo user mới

```http
POST /api/users
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Jane Doe",
  "email": "jane@example.com",
  "createdAt": "2024-01-01T11:00:00",
  "updatedAt": "2024-01-01T11:00:00"
}
```

### 4. Cập nhật user

```http
PUT /api/users/1
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john@example.com",
  "password": "newpassword123"
}
```

### 5. Xóa user

```http
DELETE /api/users/1
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "message": "User deleted successfully"
}
```

### 6. Tìm kiếm user

```http
GET /api/users/search?keyword=john&page=0&size=10
Authorization: Bearer YOUR_JWT_TOKEN
```

## 🔑 JWT Configuration

JWT được cấu hình trong `application.properties`:

```properties
# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Lưu ý:** Trong production, nên thay đổi `jwt.secret` thành một giá trị ngẫu nhiên và bảo mật hơn!

## 👤 Tài khoản mặc định

Khi ứng dụng khởi động lần đầu, một tài khoản admin mặc định sẽ được tạo tự động:

- **Email**: `admin@example.com`
- **Mật khẩu**: `admin123`

**Lưu ý**: Để thay đổi thông tin này, chỉnh sửa trong `UserManagementApplication.java`.

## 📄 Import/Export CSV

### Format CSV để Import

File CSV phải có format như sau (không có ID):

**Với header (khuyến nghị):**
```csv
Name,Email,Password
Nguyễn Văn A,nguyenvana@example.com,password123
Trần Thị B,tranthib@example.com,password456
Lê Văn C,levanc@example.com,password789
```

**Không có header:**
```csv
Nguyễn Văn A,nguyenvana@example.com,password123
Trần Thị B,tranthib@example.com,password456
Lê Văn C,levanc@example.com,password789
```

**Lưu ý:**
- Format: `Name,Email,Password` (3 cột, không có ID)
- Hệ thống sẽ tự động bỏ qua header nếu có
- Email phải unique, nếu trùng sẽ bỏ qua dòng đó

### Cách Import CSV

1. Đăng nhập vào hệ thống
2. Vào trang `/users`
3. Chọn file CSV và click "Import CSV"
4. Hệ thống sẽ import các user từ file CSV

### Cách Export CSV

1. Đăng nhập vào hệ thống
2. Vào trang `/users`
3. Click "Export CSV"
4. File CSV sẽ được tải về với tên `users_export.csv`
5. File export có format: `Name,Email,Created At,Updated At` (không có ID và Password)

## 🔒 Security

- Spring Security 6 được cấu hình để bảo vệ các trang quản lý
- Chỉ trang `/login` và các static resources là public
- REST API hiện tại cho phép truy cập không cần authentication (có thể thêm sau)
- Password được mã hóa bằng BCrypt

## 🧪 Testing

### Test với Postman hoặc cURL

**Tạo user mới:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "test123"
  }'
```

**Lấy danh sách users:**
```bash
curl http://localhost:8080/api/users?page=0&size=10
```

## 📝 Ghi chú

- Password không được trả về trong REST API response
- Email phải unique trong hệ thống
- Password tối thiểu 6 ký tự
- Tên phải có từ 2 đến 100 ký tự

## 🐛 Troubleshooting

### Lỗi kết nối MySQL

- Kiểm tra MySQL đã chạy chưa
- Kiểm tra username/password trong `application.properties`
- Đảm bảo database đã được tạo

### Lỗi port đã được sử dụng

Thay đổi port trong `application.properties`:
```properties
server.port=8081
```

### Lỗi encoding tiếng Việt

Đảm bảo database sử dụng UTF-8:
```sql
ALTER DATABASE user_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng kiểm tra:
1. Logs trong console
2. Cấu hình database
3. Dependencies trong `pom.xml`

## 📄 License

Project này được tạo cho mục đích học tập và phát triển.

---

**Chúc bạn code vui vẻ! 🚀**

