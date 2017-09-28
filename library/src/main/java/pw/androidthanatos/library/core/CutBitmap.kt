package pw.androidthanatos.library.core

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.HOGDescriptor
import org.opencv.objdetect.Objdetect
import pw.androidthanatos.library.log

/**
 * 抠图
 */
class CutBitmap {

    private val srcMat: Mat by lazy { Mat() }

    private val resultMat: Mat by lazy { Mat() }



    fun cut(path: String, result: Bitmap,matOfRect: MatOfRect){
        val img = Imgcodecs.imread(path)
        val m = matOfRect.toArray()[0]
        Imgproc.resize(img,img, Size(img.cols().toDouble(),img.rows().toDouble()))
        val firstMask = Mat()
        val bgMask = Mat()
        val fgMask = Mat()
        val source = Mat(1,1,CvType.CV_8U, Scalar((Imgproc.GC_PR_FGD).toDouble()))
        val rect = Rect(m.x,m.y,m.width,m.height)
        firstMask.create(img.size(),CvType.CV_8UC1)
//        firstMask.submat(rect).setTo(Scalar((Imgproc.GC_PR_FGD).toDouble()))
//        firstMask.setTo(Scalar(0.toDouble()))
        Imgproc.grabCut(img,firstMask,rect,bgMask,fgMask,2,Imgproc.GC_INIT_WITH_RECT)
        Core.compare(firstMask,source,firstMask,Core.CMP_EQ)
        val fg = Mat(img.size(),CvType.CV_8UC3, Scalar(255.0,255.0,255.0))
        img.copyTo(fg,firstMask)
        Utils.matToBitmap(fg,result)
    }

    fun  toCut(path: String,result: Bitmap){
        val img = Imgcodecs.imread(path)
        val mask = Mat()
        mask.create(img.size(),CvType.CV_8UC1)
        mask.setTo(Scalar(Imgproc.GC_BGD.toDouble()))
        val rect = Rect()
        val bg = Mat()
        val fg = Mat()
        rect.x = 80
        rect.y = 80
        rect.width = img.cols() - 160
        rect.height = img.rows() - 160
//        (rect as Mat).setTo(Scalar(Imgproc.GC_PR_FGD.toDouble()))
//        Imgproc.rectangle(img, Point(rect.x.toDouble(),rect.y.toDouble()),
//                Point((rect.x + rect.width).toDouble(),(rect.y + rect.height).toDouble()),
//                Scalar(0.0,255.0,0.0),2)
        Imgproc.grabCut(img,mask,rect,bg,fg,2,Imgproc.GC_INIT_WITH_RECT)
        val bin = Mat()
        val res = Mat()
        bin.create(mask.size(),CvType.CV_8UC1)
        Core.compare(mask,bin,mask,Core.CMP_EQ)
        img.copyTo(res,bin)
        Utils.matToBitmap(bin,result)
    }

    fun getPeople(path: String = "",result: Bitmap){
        val temp = result.copy(Bitmap.Config.ARGB_8888,true)
        val originalMat = Mat(temp.height,temp.width,CvType.CV_8U)
        Utils.bitmapToMat(temp,originalMat)
        val currentBitmap = result.copy(Bitmap.Config.ARGB_8888,false)

        val gray = Mat()
        val people = Mat()

        Imgproc.cvtColor(originalMat,gray,Imgproc.COLOR_BGR2GRAY)
        val hog = HOGDescriptor()
        hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector())

        val faces = MatOfRect()
        val weights = MatOfDouble()

        hog.detectMultiScale(gray,faces,weights)
        originalMat.copyTo(people)

        //绘制行人框框
        val faceArray = faces.toArray()
        faceArray.forEach {
            Imgproc.rectangle(people,it.tl(),it.br(), Scalar(100.0),3)
        }
        Utils.matToBitmap(people,result)
    }

    fun test(path: String,result: Bitmap){
        val img = Imgcodecs.imread(path)
        val gray = Mat()
        val hier = Mat()
        Imgproc.cvtColor(img,gray,Imgproc.COLOR_BGR2GRAY)
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(gray,contours,hier,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE)
        Utils.matToBitmap(gray,result)
        var index = 0
        var maxin = Imgproc.contourArea(contours[0])
        (1 until contours.size).forEach {
            var temp = 0.0
            temp = Imgproc.contourArea(contours[it])
            if(maxin <temp){
                maxin = temp
                index = it
            }
            log("${contours[it].toArray().size}")
        }

    log("${contours.size}")
        log("${contours[0].toArray().size}")
        contours[0].toArray().forEach {
            log("  x: ${it.x}    y: ${it.y} ")
        }
        val drawing = Mat.zeros(img.size(),CvType.CV_8UC1)
        Imgproc.drawContours(drawing,contours,index, Scalar(255.0),1)

    }












}