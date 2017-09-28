package pw.androidthanatos.library.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opencv.R
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import pw.androidthanatos.library.log
import pw.androidthanatos.library.openBitmapHandler
import java.io.File
import java.io.FileOutputStream

/**
 * 人脸识别
 */
class FaceRecognition(private val context: Context) {

    private lateinit var mFile: File

    init {
        writeXml()
    }

    /**
     * 将人脸识别的文件写入本地
     */
    private fun writeXml(){

        val stream = context.resources.openRawResource(R.raw.lbpcascade_frontalface)
        val cascadeDir = context.getDir("cascade",Context.MODE_PRIVATE)
        val cascadeFile = File(cascadeDir,"lbpcascade_frontalface.xml")
        val fos = FileOutputStream(cascadeFile)
        fos.write(stream.use { it.readBytes() })
        fos.flush()
        fos.close()
        mFile = cascadeFile

    }


    /**
     * 扫描Bitmap中的人物头像
     * @param bitmap Bitmap
     * @param marker 是否在原图上进行人脸标注
     * @return MatOfRect 标注的头像的坐标和大小
     */
    fun scanFace(bitmap: Bitmap, marker: Boolean): MatOfRect?{
        val srcMat = Mat()
        Utils.bitmapToMat(bitmap,srcMat)
        val faceDetector = CascadeClassifier(this.mFile.absolutePath)
        if (faceDetector.empty()){
            log("读取初始化文件失败！")
            return null
        }else{
            val faceDetections = MatOfRect()
            faceDetector.detectMultiScale(srcMat,faceDetections)
            //在图片中进行标注
            if (marker){
                for (rect in faceDetections.toArray()){
                    Imgproc.rectangle(srcMat,rect.tl(),rect.br(),
                            Scalar(0.toDouble(), 255.toDouble(), 0.toDouble(), 255.toDouble()),3 )
                }
                Utils.matToBitmap(srcMat,bitmap)
            }

            return faceDetections
        }

    }


    /**
     * 返回原图和标注的头像的坐标和大小
     */
    fun scanFace(path: String, marker: Boolean): FaceResult?{
        val bitmap = BitmapFactory.decodeFile(path)
        val srcMat = Mat()
        Utils.bitmapToMat(bitmap,srcMat)
        val faceDetector = CascadeClassifier(this.mFile.absolutePath)
        if (faceDetector.empty()){
            log("读取初始化文件失败！")
            return null
        }else{
            val faceDetections = MatOfRect()
            faceDetector.detectMultiScale(srcMat,faceDetections)
            //在图片中进行标注
            if (marker){
                for (rect in faceDetections.toArray()){
                    Imgproc.rectangle(srcMat,rect.tl(),rect.br(),
                            Scalar(0.toDouble(), 255.toDouble(), 0.toDouble(), 255.toDouble()),3 )
                }
                Utils.matToBitmap(srcMat,bitmap)
            }

            return FaceResult(bitmap,faceDetections)
        }

    }


    data class FaceResult(val bitmap: Bitmap, val matOfRect: MatOfRect?)

}