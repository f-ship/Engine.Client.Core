package ship.f.engine.client.lib.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.request.WebRequestInterceptResult.Allow
import com.multiplatform.webview.request.WebRequestInterceptResult.Reject
import com.multiplatform.webview.web.*
import ship.f.engine.client.core.Slice
import ship.f.engine.client.lib.web.WebSubPub.WebState
import ship.f.engine.shared.utils.serverdrivenui.action.Action.SendUrl.Intercept.InterceptAction.Param

class WebSlice : Slice<WebState, WebSubPub>(
    subPubClass = WebSubPub::class
) {
    @Composable
    override fun EntryPoint(state: MutableState<WebState>) {
        state.value.url?.let {
            var showWebView by mutableStateOf(true)
            if (showWebView) {
                Box(modifier = Modifier.background(Color.White)) {
                    val state = rememberWebViewState(it.url)
                    val navigator = it.urlIntercept?.let { intercept ->
                        rememberWebViewNavigator(
                            requestInterceptor = object : RequestInterceptor {
                                override fun onInterceptUrlRequest(
                                    request: WebRequest,
                                    navigator: WebViewNavigator
                                ): WebRequestInterceptResult {
                                    return if (request.url.contains(intercept.url)) {
                                        when (intercept.action) {
                                            is Param -> Reject.also { showWebView = false }
                                        }
                                    } else {
                                        Allow
                                    }
                                }
                            }
                        )
                    }
                    val loadingState = state.loadingState
                    if (loadingState is LoadingState.Loading) {
                        LinearProgressIndicator( // TODO this can definitely be improved as it's not very clear
                            progress = loadingState.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    WebView(
                        state = state,
                        navigator = navigator ?: rememberWebViewNavigator()
                    )
                }
            }
        }
    }

    @Composable
    override fun notReadyEntryPoint(state: MutableState<WebState>): @Composable (() -> Unit) {
        return {
            println("WebSlice not ready yet")
        }
    }
}