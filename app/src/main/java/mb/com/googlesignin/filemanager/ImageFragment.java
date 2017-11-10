package mb.com.googlesignin.filemanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import mb.com.googlesignin.MainActivity.GooglePlusSignIn;

import mb.com.googlesignin.Manifest;
import mb.com.googlesignin.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static mb.com.googlesignin.R.id.seeImage;

/**
 * Created by Anshul on 08-11-17.
 */

public class ImageFragment extends Fragment
{
    ImageView imageView;
    public static final int PICK_MULTIPLE_IMAGE=4;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       // return inflater.inflate(R.layout.filemanagerlayout,container,false);
        View view=inflater.inflate(R.layout.filemanagerlayout,container,false);
        imageView=(ImageView)view.findViewById(R.id.seeImage);
        return view;
    }

    public void openGallery()
    {
        Intent imagePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);//,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//Intent.ACTION_OPEN_DOCUMENT
        imagePicker.addCategory(Intent.CATEGORY_OPENABLE);
        imagePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePicker.setAction(Intent.ACTION_GET_CONTENT);
        imagePicker.setType("image/*");
        startActivityForResult(Intent.createChooser(imagePicker,"Select Picture"),PICK_MULTIPLE_IMAGE);
    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_MULTIPLE_IMAGE)
        {
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator);
            String imagepath=file.getPath();
            Uri imageUri= Uri.parse(imagepath);

            try {
                Uri image= data.getData();
//                Glide.with(this).load(image).asBitmap().into(new SimpleTarget<Bitmap>(3000, 3000) {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        Drawable drawable = new BitmapDrawable(resource);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            imageView.setBackground(drawable);
//                        }
//                    }
//                });
                Glide.with(this)
                        .load(image)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);//.override(3000, 3000)
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
        }

}
