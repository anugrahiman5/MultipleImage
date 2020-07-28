package com.example.multipleimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    Button btn_select_image,btn_get_image;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Uri clipImageUri;
    private StorageReference mStorageRef;
    long maxId = 0;
    ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_select_image = findViewById(R.id.btn_upload_image);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.image_recycler);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btn_get_image = findViewById(R.id.btn_get_image);
        imageList = new ArrayList<>();

        btn_select_image.setOnClickListener(view ->
        {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);

                return;
            }

            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setType("image/*");
            startActivityForResult(intent,1);
        });

        btn_get_image.setOnClickListener(view ->
        {

            DatabaseReference reff = FirebaseDatabase.getInstance().getReference("image");

            reff.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                   for (DataSnapshot ds : dataSnapshot.getChildren())
                   {
                       imageList.add(ds.child("imageUrl").getValue(String.class));
                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

            Log.d("imagelist",imageList.toString());
            ImageAdapter imageAdapter = new ImageAdapter(this,imageList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(imageAdapter);
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
        {

            ClipData clipData = data.getClipData();

            if (clipData != null)
            {
                for (int i = 0; i < clipData.getItemCount(); i++)
                {
                    clipImageUri = clipData.getItemAt(i).getUri();

                    if (clipImageUri != null) {

                        StorageReference ref = mStorageRef.child("images/"+ UUID.randomUUID().toString());

                        ref.putFile(clipData.getItemAt(i).getUri())
                                .addOnSuccessListener(
                                        taskSnapshot -> {

                                            ref.getDownloadUrl().addOnSuccessListener(uri ->
                                            {
                                               DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference("image");
                                               imagestore.addValueEventListener(new ValueEventListener()
                                               {
                                                   @Override
                                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                   {
                                                        if (dataSnapshot.exists())
                                                        {
                                                            maxId = (dataSnapshot.getChildrenCount());
                                                        }
                                                   }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });
                                                HashMap<String,String> hashMap = new HashMap<>();
                                                hashMap.put("imageUrl",String.valueOf(uri));

                                                imagestore.child(String.valueOf(maxId +1)).setValue(hashMap).addOnSuccessListener(aVoid ->
                                                {

                                                });
                                                Toast.makeText(this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                                            });
//
                                        })

                                .addOnFailureListener(e -> {

                                    Toast.makeText(MainActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                });
                    }
                }
            }
        }
    }
}
