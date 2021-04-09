package com.mariusbudin.hvor.data.common

import android.content.Context
import com.mariusbudin.hvor.core.exception.Failure
import com.mariusbudin.hvor.core.functional.Either
import com.mariusbudin.hvor.data.common.platform.NetworkHandler
import retrofit2.Call

class BaseRepository {

    open class Remote(private val networkHandler: NetworkHandler) {

        protected fun <T, R> request(
            call: Call<T>,
            transform: (T) -> R,
            defaultValue: R
        ): Either<Failure, R> {
            return try {
                val response = call.execute()
                when (response.isSuccessful) {
                    true -> Either.Right(response.body()?.let(transform) ?: defaultValue)
                    false -> Either.Left(Failure.ServerError)      //FIXME add controlled backend error using the info in Meta object
                }
            } catch (exception: Throwable) {
                Either.Left(Failure.ServerError)
            }
        }
    }

    open class Local(private val context: Context)
}
