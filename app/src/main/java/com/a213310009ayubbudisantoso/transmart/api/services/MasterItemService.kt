import com.a213310009ayubbudisantoso.transmart.api.model.ItemResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MasterItemService {
    @POST("apiMobile/master-item")
    fun sendBarcode(@Body body: JsonObject): Call<ItemResponse>
}
