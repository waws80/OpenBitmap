package pw.androidthanatos.library

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import pw.androidthanatos.library.core.Bitmap2Gray
import pw.androidthanatos.library.core.CutBitmap
import pw.androidthanatos.library.core.FaceRecognition
import java.lang.ref.WeakReference

/**
 * OpenBitmap 基于OpenCV编写的图片处理库
 */
object OpenBitmap {

    private var mContextWrf: WeakReference<Context>? = null

    private var mLoaderCallBack: BaseLoaderCallback? = null

    /**
     * 安装处理库
     */
    @JvmStatic
    fun install(context: Context): OpenBitmap{
        mContextWrf = WeakReference(context)
        mLoaderCallBack = initLoadCall(mContextWrf?.get()!!)
        if (OpenCVLoader.initDebug()){
            mLoaderCallBack?.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }else{
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,
                    mContextWrf?.get(), mLoaderCallBack)
        }
        return this
    }


    /**
     * 是否开启测试模式
     */
    fun debug(debug: Boolean){
        DEBUG = debug
    }

    /**
     * 初始化回调函数
     */
    private fun initLoadCall(context: Context) =
            object : BaseLoaderCallback(context){
                override fun onManagerConnected(status: Int) {

                    when(status){
                        BaseLoaderCallback.SUCCESS ->{
                            log("OpenBitmap加载成功")
                            openBitmapHandler = Handler(Looper.getMainLooper())
                        }
                        else ->{
                            super.onManagerConnected(status)
                            log("OpenBitmap加载失败")
                        }
                    }
                }

            }


    /**
     * 进行人脸识别
     */
    fun faceRecogniticon() = FaceRecognition(mContextWrf?.get()!!)

    /**
     * 对图片进行灰化
     */
    fun gray() = Bitmap2Gray()

    fun cut() = CutBitmap()

    /**
     * 卸载处理库
     */
    @JvmStatic
    fun Uninstall(){
        if (null != mContextWrf){
            mContextWrf?.clear()
            mContextWrf = null
        }
        if (null != openBitmapHandler){
            openBitmapHandler = null
        }
    }
}