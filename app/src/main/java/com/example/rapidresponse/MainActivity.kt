package com.example.rapidresponse

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.form.*
import kotlinx.android.synthetic.main.form.view.*
import kotlinx.android.synthetic.main.isian.view.*
import kotlinx.android.synthetic.main.tv_isian.view.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var imageUri1: Uri? = null //file gambar dari kamera
    var imageUri2: Uri? = null //file gambar dari kamera
    var imageUri3: Uri? = null //file gambar dari kamera
    var fileUri: Uri? = null //file gambar dari galeri
    lateinit var bitmap1: Bitmap
    lateinit var bitmap2: Bitmap
    lateinit var bitmap3: Bitmap
    lateinit var decoded1: Bitmap
    lateinit var decoded2: Bitmap
    lateinit var decoded3: Bitmap
    val REQUEST_CAMERA = 0
    val SELECT_FILE = 0

    var bitmap_size = 40
    var max_resolution_image = 800

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkGeneralPermissions()

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

        btCamera1.setOnClickListener {
            selectImage(formHasil.fotoRecommendator1, "1")
        }
        btCamera2.setOnClickListener {
            selectImage(formHasil.fotoRecommendator2, "2")
        }
        btCamera3.setOnClickListener {
            selectImage(formHasil.fotoRecommendator3, "3")
        }

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
            val pagesize = Rectangle(1050f, 1700f)
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

    private fun selectImage(iv: ImageView, string: String) {
        val str = string
        iv.setImageResource(0)
        val items = arrayOf("Kamera (Miringkan kamera)", "Pilih dari galeri", "Batal")
        val builder = AlertDialog.Builder(this).setTitle("Tambahkan Foto").setIcon(R.drawable.ic_launcher_rapidresponse)
        builder.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            if (items[which].equals("Kamera (Miringkan kamera)")){
                if (str.equals("1")){
                    openCamera1()
                } else if (str.equals("2")){
                    openCamera2()
                } else if (str.equals("3")) {
                    openCamera3()
                } else {
                    Toast.makeText(this, "kamera error", Toast.LENGTH_SHORT).show()
                }
            } else if (items[which].equals("Pilih dari galeri")){
                intent = Intent().setType("image/*")
                intent = Intent(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), str.toInt())
            } else if (items[which].equals("Batal")){
                dialog.dismiss()
            }
        })
        builder.show()
    }

    private fun getOutputMediaFile(): File? {
        var mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Form")
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.e("Monitoring", "Oops! Gagal membuat direktori monitoring")
                return null
            }
        }
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var mediaFile = File(mediaStorageDir.path + File.separator + "IMG_Form_" + timeStamp + ".jpg")

        return mediaFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CAMERA){
                Glide.with(this).load(imageUri1).into(fotoRecommendator1)
                Glide.with(this).load(imageUri2).into(fotoRecommendator2)
                Glide.with(this).load(imageUri3).into(fotoRecommendator3)

            } else if (requestCode == 1 && data != null && data.data != null) {
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                    setToImageView1(getResizeBitmap(bitmap1, max_resolution_image.toFloat()))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (requestCode == 2 && data != null && data.data != null) {
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                    setToImageView2(getResizeBitmap(bitmap2, max_resolution_image.toFloat()))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (requestCode == 3 && data != null && data.data != null) {
                try {
                    bitmap3 = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                    setToImageView3(getResizeBitmap(bitmap3, max_resolution_image.toFloat()))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openCamera1() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri1 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    private fun openCamera2() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri2 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    private fun openCamera3() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri3 = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri3)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    private fun setToImageView1(bmp: Bitmap){
        var bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes)
        decoded1 = BitmapFactory.decodeStream(ByteArrayInputStream(bytes.toByteArray()))

        Glide.with(this).load(decoded1).into(formHasil.fotoRecommendator1)
    }

    private fun setToImageView2(bmp: Bitmap){
        var bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes)
        decoded2 = BitmapFactory.decodeStream(ByteArrayInputStream(bytes.toByteArray()))

        Glide.with(this).load(decoded2).into(formHasil.fotoRecommendator2)
    }

    private fun setToImageView3(bmp: Bitmap){
        var bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes)
        decoded3 = BitmapFactory.decodeStream(ByteArrayInputStream(bytes.toByteArray()))

        Glide.with(this).load(decoded3).into(formHasil.fotoRecommendator3)
    }

    fun getResizeBitmap(image: Bitmap, maxSize: Float): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize.toInt()
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize.toInt()
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    //fungsi untuk check permissions
    private fun checkGeneralPermissions(){
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
            Manifest.permission.CAMERA)
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    if (shouldProvideRationale){
                    }
                }
            }).check()
    }

    override fun onBackPressed() {
        val builder =
            androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Apakah anda yakin ingin keluar dari Aplikasi?")
        builder.setCancelable(true)
        builder.setNegativeButton(
            "Tidak"
        ) { dialog, which -> dialog.cancel() }
        builder.setPositiveButton(
            "Ya"
        ) { dialog, which -> finishAffinity() }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}
