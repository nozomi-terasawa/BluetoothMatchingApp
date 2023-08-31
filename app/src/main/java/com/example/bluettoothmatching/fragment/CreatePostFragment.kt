package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.MainActivity
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.database.FireBaseStorage
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.database.imageRef
import com.example.bluettoothmatching.databinding.FragmentCreatePostBinding
import com.example.bluettoothmatching.navController

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()
    private val storage = FireBaseStorage()
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
        // navigationDrawerの非表示
        val mainActivity = requireActivity() as MainActivity
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val action = mainActivity.getActionBarDrawerToggle()
        action.isDrawerIndicatorEnabled = false
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //トップメニューの非表示
        val toolbar = mainActivity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        fireStore.currentPoint(binding, requireContext())

        var color: String = "FFFFFF"
        var selectColorFlag_10 = false
        var selectColorFlag_20 = false


        binding.pinkButton.setOnClickListener {
            // todo ポイントが１０以上じゃないと押せないようにする
            color = "FFC0CB"
            selectColorFlag_10 = true
            binding.cardView.setBackgroundColor(Color.parseColor("#FFC0CB"))
        }

        binding.blueButton.setOnClickListener {
            color = "AFEEEE"
            selectColorFlag_10 = true
            binding.cardView.setBackgroundColor(Color.parseColor("#AFEEEE"))
        }

        binding.greenButton.setOnClickListener {
            color = "98FB98"
            selectColorFlag_10 = true
            binding.cardView.setBackgroundColor(Color.parseColor("#98FB98"))
        }

        binding.gradientButton1.setOnClickListener {
            color = "gradient1"
            selectColorFlag_20 = true
            binding.cardView.setBackgroundResource(R.drawable.gradient1)
        }

        binding.gradientButton2.setOnClickListener {
            color = "gradient2"
            selectColorFlag_20 = true
            binding.cardView.setBackgroundResource(R.drawable.gradient2)
        }

        binding.gradientButton3.setOnClickListener {
            color = "gradient3"
            selectColorFlag_20 = true
            binding.cardView.setBackgroundResource(R.drawable.gradient3)
        }

        binding.postButton.setOnClickListener {
            val body = binding.createBody.text.toString()

            if (color != "FFFFFF" && selectColorFlag_10 == true) {
                val builder = AlertDialog.Builder(requireContext()) // FragmentではrequireContext()を使う
                .setTitle("ポイントの使用")
                .setMessage("10ポイントを使用します")
                .setPositiveButton("はい") { dialog, which ->
                    // Yesが押された時の挙動
                    fireStore.post(body, color)
                    fireStore.usePoint()
                }
                .setNegativeButton("いいえ") { dialog, which ->
                    // Noが押された時
                    dialog.dismiss()
                }
                builder.show()
            } else if (color != "FFFFFF" && selectColorFlag_20 == true) {
                val builder = AlertDialog.Builder(requireContext()) // FragmentではrequireContext()を使う
                    .setTitle("ポイントの使用")
                    .setMessage("20ポイントを使用します")
                    .setPositiveButton("はい") { dialog, which ->
                        // Yesが押された時の挙動
                        fireStore.post(body, color)
                        fireStore.usePoint2()
                    }
                    .setNegativeButton("いいえ") { dialog, which ->
                        // Noが押された時
                        dialog.dismiss()
                    }
                builder.show()
            } else {
                fireStore.post(body, color)
                    /*
                if (selectColorFlag == true) {
                    fireStore.usePoint()
                }

                     */
            }
            if (imageUri != null) {
                Log.d("getImage", "画像がnull出はないのでアップします")

                storage.uploadImageToFirebaseStorage(imageUri!!, imageRef.toString())
                // imageUri = null
            } else {
                val action = CreatePostFragmentDirections.actionCreatePostFragment2ToProfileListFragment()
                navController.navigate(action)
            }
        }

        binding.advertiseButton.setOnClickListener {
            val body = binding.createBody.text.toString()
            fireStore.advertise(body, color)

            if (imageUri != null) {
                storage.uploadImageToFirebaseStorage(imageUri!!, imageRef.toString())
            } else {
                val action = CreatePostFragmentDirections.actionCreatePostFragment2ToProfileListFragment()
                navController.navigate(action)
            }
        }

        binding.insertImage.setOnClickListener {
            selectPhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val READ_REQUEST_CODE: Int = 42
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        imageUri = uri // 画像のURI取得
                            //binding.image.setImageURI(uri)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
}
