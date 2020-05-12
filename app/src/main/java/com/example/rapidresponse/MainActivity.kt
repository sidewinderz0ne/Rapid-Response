package com.example.rapidresponse

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.InputType
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.form.*
import kotlinx.android.synthetic.main.form.view.*
import kotlinx.android.synthetic.main.isian.view.*
import kotlinx.android.synthetic.main.tv_isian.view.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setText(dateVisit, "Date Visit", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(estate, "Estate", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(afdBlok, "Afd/Blok", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(recommendator, "Recmmndtr", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(accompaniedBy, "Accompd By", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(subject, "Subject", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))
        setText(no, "No", InputType.TYPE_TEXT_VARIATION_PERSON_NAME.or(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS))

        problemInvestigation.tvData.text = "Problems"
        problemInvestigation.etData.hint = "Problems"

        recommendation.tvData.text = "Recoms"
        recommendation.etData.hint = "Recoms"

        tvDateVisit.tvDataDes.text = "Date Visit"
        tvEstate.tvDataDes.text = "Estate"
        tvAfdBlok.tvDataDes.text = "Afd/Blok"
        tvNo.tvDataDes.text = "No."
        tvRecommendator.tvDataDes.text = "Recommendator"
        tvAccompaniedBy.tvDataDes.text = "Accompanied By"
        tvSubject.tvDataDes.text = "Subject"

        Glide.with(this)//GLIDE LOGO FOR LOADING LAYOUT
            .load(R.drawable.logo_png_white)
            .into(logo_ssms)
        lottie.setAnimation("loading_circle.json")//ANIMATION WITH LOTTIE FOR LOADING LAYOUT
        lottie.loop(true)
        lottie.playAnimation()
        btSS.setOnClickListener {
            if (dateVisit.etData.text.isNotEmpty() || estate.etData.text.isNotEmpty() || afdBlok.etData.text.isNotEmpty() || recommendator.etData.text.isNotEmpty() ||
                accompaniedBy.etData.text.isNotEmpty() || subject.etData.text.isNotEmpty() || no.etData.text.isNotEmpty() || problemInvestigation.etData.text.isNotEmpty() || recommendation.etData.text.isNotEmpty()){
                tvDateVisit.tvDataHasil.text = dateVisit.etData.text
                        tvEstate.tvDataHasil.text = estate.etData.text
                tvAfdBlok.tvDataHasil.text = afdBlok.etData.text
                val recs = recommendator.etData.text.toString().split(",").toTypedArray()
                val receive = accompaniedBy.etData.text.toString().split(",").toTypedArray()
                tvRecommendator.tvDataHasil.text = recommendator.etData.text
                tvAccompaniedBy.tvDataHasil.text = accompaniedBy.etData.text
                tvRecommendator1.text = recs[0]
                try { tvRecommendator2.text = recs[1] } catch (e:Exception){ tvRecommendator2.text = "............" }
                try { tvRecommendator3.text = recs[2] } catch (e:Exception){ tvRecommendator3.text = "............" }
                tvReceivedBy1.text = receive[0]
                try { tvReceivedBy2.text = receive[1] } catch (e:Exception){ tvReceivedBy2.text = "............" }
                try { tvReceivedBy3.text = receive[2] } catch (e:Exception){ tvReceivedBy3.text = "............" }
                tvSubject.tvDataHasil.text = subject.etData.text
                tvNo.tvDataHasil.text = no.etData.text
                tvProblem.text = problemInvestigation.etData.text
                tvRecommendation.text = recommendation.etData.text
                sharePDF()
            } else {
                Toast.makeText(this, "Ada kolom yang belum diisi", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun setText(view: View, judul: String, inputType: Int){
        view.tvData.text = judul
        view.etData.hint = judul
        view.etData.inputType = inputType
    }

    fun sharePDF() {
        val editestateStr: String = estate.etData.text.toString().replace("/", " ")
        val editblok1Str: String = afdBlok.etData.text.toString().replace("/", " ")
        val sdf =
            SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val now = sdf.format(Date())
        val iv =
            findViewById<HorizontalScrollView>(R.id.svHorizontal)
        val bm = Bitmap.createBitmap(
            iv.getChildAt(0).width,
            iv.getChildAt(0).height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(bm)
        iv.getChildAt(0).draw(c)
        val dirPath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dirPath, "share.JPEG")
        try {
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
        }
        try {
            val pagesize = Rectangle(1100f, 1400f)
            val document = Document(pagesize)
            PdfWriter.getInstance(document, FileOutputStream("$dirPath${File.separator}RapidResponse $editestateStr $editblok1Str $now.PDF")) //  Change pdf's name.
            document.open()
            val img = Image.getInstance("$dirPath/share.JPEG")
            val scaler = (document.pageSize
                .height - document.topMargin() - document.bottomMargin()) / img.height * 100
            img.scalePercent(scaler)
            img.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP
            document.add(img)
            document.close()
            Toast.makeText(this, "Membagikan File PDF", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Terjadi Kesalahan error: $dirPath${File.separator}RapidResponse $editestateStr $editblok1Str $now.PDF", Toast.LENGTH_LONG).show()
        }
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val outputFile = File("$dirPath${File.separator}RapidResponse $editestateStr $editblok1Str $now.PDF")
        val uri = Uri.fromFile(outputFile)
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
            .setType("application/pdf")
            .setType("text/plain")
            .putExtra(
                Intent.EXTRA_TEXT,
                "Taksasi $editestateStr $editblok1Str $now"
            )
            .putExtra(Intent.EXTRA_SUBJECT, "")
            .putExtra(Intent.EXTRA_TEXT, "")
            .putExtra(Intent.EXTRA_STREAM, uri)
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_LONG).show()
        }
    }
}
