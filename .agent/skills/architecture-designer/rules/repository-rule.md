# Quy Tắc Thiết Kế Repository Layer

Khi thiết kế và triển khai Data Layer (cụ thể là Repository Pattern), yêu cầu bắt buộc phải tuân theo các quy tắc sau:

1. **Kiểu trả về (Return Type):** 
   Tất cả các kết quả trả về từ các hàm trong Repository (ví dụ lời gọi API, truy vấn Database) cần được bọc trong một `sealed class` (hoặc `sealed interface`) đại diện cho Result. Result này bắt buộc phải bao gồm 3 trạng thái: `Success`, `Error`, và `Loading`.
   - **Đối với các tác vụ 1-shot (gọi 1 lần rồi kết thúc):** Có thể trả về trực tiếp `DataResult<T>` thông qua `suspend fun`.
   - **Đối với dữ liệu dạng List hoặc object cần cập nhật/lắng nghe realtime ngay trên màn hình:** Bắt buộc trả về `Flow<DataResult<T>>` để UI có thể lắng nghe và tự động phản hồi khi có sự thay đổi.

2. **Dependency Injection (DI) cho Implementation:**
   - Khi inject implementation của Repository vào các layer khác (như ViewModel), cần tham khảo và ưu tiên sử dụng cơ chế DI hiện có trong dự án (ví dụ: Hilt, Dagger, Koin).
   - Nếu dự án chưa có bất kỳ framework DI nào, yêu cầu thực hiện DI thủ công (Manual DI) bằng cách khởi tạo và truyền implementation qua constructor (thường thông qua một Factory hoặc Composition Root). Tuyệt đối không khởi tạo cứng (hardcode instance) bên trong constructor hoặc thân của các class sử dụng.

### Ví dụ Minh Họa

```kotlin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

// 1. Định nghĩa DataResult với 3 state: Success, Error, Loading
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val exception: Exception) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}

// 2. Định nghĩa Interface cho Repository
interface ExampleRepository {
    // Trả về kết quả đơn lẻ (tác vụ 1-shot, ví dụ: lấy chi tiết User từ API)
    suspend fun getUserProfile(userId: String): DataResult<User>
    
    // Trả về luồng dữ liệu liên tục (ví dụ: observe danh sách từ Database)
    fun observeItems(): Flow<DataResult<List<String>>>
}

// 3. Triển khai Implementation của Repository
// Đảm bảo các dependency (như DataSource, DAO) cũng được inject qua constructor
class ExampleRepositoryImpl(
    private val apiService: ApiService,
    private val localDao: LocalDao
) : ExampleRepository {

    override suspend fun getUserProfile(userId: String): DataResult<User> {
        return try {
            val response = apiService.getUser(userId)
            DataResult.Success(response)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun observeItems(): Flow<DataResult<List<String>>> {
        return localDao.observeItems()
            .map { items -> DataResult.Success(items) as DataResult<List<String>> }
            .onStart { emit(DataResult.Loading) }
            .catch { e -> 
                val exception = e as? Exception ?: Exception(e)
                emit(DataResult.Error(exception)) 
            }
    }
}

// 4. Ví dụ Inject (Sử dụng Hilt/Dagger nếu dự án có sẵn)
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExampleRepository(
        impl: ExampleRepositoryImpl
    ): ExampleRepository
}
*/
```
