# 🔧 API Models Fix - Khớp với Backend

## ❌ Vấn đề trước đây:

```kotlin
// SAI - Không khớp với backend
data class ApiResponse<T>(
    val code: Int,        // ❌ Backend trả về String
    val message: String,
    val data: T?          // ❌ Backend dùng field "result"
)

// Check sai
if (response.code == 200) { ... }  // ❌ So sánh Int
```

## ✅ Đã fix:

```kotlin
// ĐÚNG - Khớp với backend
data class ApiResponse<T>(
    @SerializedName("code")
    val code: String,     // ✅ String như backend
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("result")  // ✅ "result" không phải "data"
    val result: T?
)

// Check đúng
if (response.code == "200") { ... }  // ✅ So sánh String
```

## 📋 Backend ApiResponse (từ common module):

```java
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private String code = "200";  // ← String!
    
    private String message;
    private T result;             // ← "result" không phải "data"!
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code("200")
                .message(message)
                .result(data)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
```

## 🔄 Các thay đổi đã làm:

### 1. **ApiModels.kt** - FIXED ✅
- ✅ `code: String` (was `Int`)
- ✅ `result: T?` (was `data: T?`)
- ✅ `resourceUrls: List<String>?` trong Post models (was `imageUrls` và `videoUrls` riêng)

### 2. **Repositories.kt** - FIXED ✅
Tất cả checks đã được sửa:

```kotlin
// AuthRepository
if (response.code == "200" && response.result != null) {
    Result.success(response.result)
}

// UserRepository  
if (response.code == "200" && response.result != null) {
    Result.success(response.result)
}

// PostRepository
if (response.code == "200" && response.result != null) {
    Result.success(response.result)
}

// ChatRepository
if (response.code == "200" && response.result != null) {
    Result.success(response.result)
}
```

## 📊 Ví dụ Response từ Backend:

### Login Success:
```json
{
  "code": "200",
  "message": "Login success",
  "result": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "sessionId": "uuid-here",
    "user": {
      "id": "user-id",
      "email": "user@example.com",
      "firstName": "Nguyen",
      "lastName": "Van A"
    }
  }
}
```

### Get Feed Success:
```json
{
  "code": "200",
  "message": "Get posts successfully",
  "result": {
    "content": [
      {
        "id": "post-id",
        "title": "Post title",
        "content": "Post content",
        "type": "IMAGE",
        "resourceUrls": [
          "http://72.60.198.235:9000/forum-kma-public/images/image1.jpg",
          "http://72.60.198.235:9000/forum-kma-public/images/image2.jpg"
        ],
        "reactionCount": 10,
        "commentCount": 5
      }
    ],
    "page": 0,
    "limit": 10,
    "total": 100,
    "totalPages": 10
  }
}
```

### Error Response:
```json
{
  "code": "401",
  "message": "Unauthorized - Invalid credentials"
}
```

## ✅ Đã test với backend:

- ✅ Login API
- ✅ Register API
- ✅ Get Feed API
- ✅ Get Conversations API
- ✅ Error handling

## 🎯 Kết quả:

Bây giờ Android app sẽ parse đúng response từ backend và hoạt động chính xác!

---

**Ngày fix**: 30/12/2024  
**Files đã sửa**: 
- `data/model/ApiModels.kt`
- `data/repository/Repositories.kt`
