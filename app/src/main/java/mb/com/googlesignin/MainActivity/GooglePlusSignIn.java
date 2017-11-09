package mb.com.googlesignin.MainActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import mb.com.googlesignin.DataBase.DatabaseHelper;
import mb.com.googlesignin.filemanager.FileManagerActivity;
import mb.com.googlesignin.UserRelatedClasses.LoginUserDetails;
import mb.com.googlesignin.R;

import static android.R.attr.data;
import static mb.com.googlesignin.R.id.ProfilePic;

public class GooglePlusSignIn extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GooglePlusSignIn.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    public GoogleApiClient mGoogleApiClient;
    public Button gmailSignInButton, gmailSignOutButton, fb_signinButton, fb_signoutButton,filemanagerbutton;
    public TextView loginStatus, userEmail, userName, optionText;
    public ImageView userPic;
    CallbackManager callbackManager;
    DatabaseHelper dbhelper = new DatabaseHelper(this);
    LoginUserDetails userdetail = new LoginUserDetails();
    public final int PICK_MULTIPLE_IMAGE=4;
    ImageView seeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_plus_sign_in);


        FileManagerActivity filemanager = new FileManagerActivity();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainXMlFile, filemanager, "GoToFile");
        //transaction.replace(R.id.seeImage,filemanager);
        transaction.commit();
        //Finding Controls by Id from corresponding XML file
        gmailSignInButton = (Button) findViewById(R.id.gmail_signinbutton);
        gmailSignOutButton = (Button) findViewById(R.id.gmail_signoutbutton);
        loginStatus = (TextView) findViewById(R.id.login_status);
        userName = (TextView) findViewById(R.id.UserName);
        userEmail = (TextView) findViewById(R.id.UserEmail);
        userPic = (ImageView) findViewById(ProfilePic);
        optionText = (TextView) findViewById(R.id.OptionText);
        fb_signinButton = (Button) findViewById(R.id.fb_signinbutton);
        fb_signoutButton = (Button) findViewById(R.id.fb_signoutbutton);
        filemanagerbutton=(Button)findViewById(R.id.filemangaerbutton);

        seeImage=(ImageView)findViewById(R.id.seeImage);
        //Changing color of text on button
        fb_signinButton.setTextColor(Color.parseColor("#c5f5f0"));
        fb_signoutButton.setTextColor(Color.parseColor("#c5f5f0"));
        gmailSignInButton.setTextColor(Color.parseColor("#c5f5f0"));
        gmailSignOutButton.setTextColor(Color.parseColor("#c5f5f0"));


        gmailSignInButton.setOnClickListener(this);
        gmailSignOutButton.setOnClickListener(this);
        fb_signinButton.setOnClickListener(this);
        fb_signoutButton.setOnClickListener(this);
        filemanagerbutton.setOnClickListener(this);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        setFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        loginStatus.setText("Login Cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        loginStatus.setText("Check your internet connection please");
                    }
                });

        userdetail = dbhelper.isLoggedIn();
        if ((userdetail.getAppname()).equals("Google")) {
            signIn();
        } else if ((userdetail.getAppname()).equals("Facebook")) {
            LoginManager.getInstance().logInWithReadPermissions(GooglePlusSignIn.this,
                    Arrays.asList("public_profile", "user_friends", "email"));

        }
    }


    boolean connected = false;

    public boolean check_InternnetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            return connected;
        }
        return connected;
    }


        public void signIn(){
            check_InternnetConnection();
                if(connected) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
                else
                {
                    Toast.makeText(this,"Check your internet connection please",Toast.LENGTH_SHORT).show();
                }

                      }

    public void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
        Toast.makeText(this, "You have successfully logged Out", Toast.LENGTH_SHORT).show();
        dbhelper.delete();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    public void handleSignInResult(GoogleSignInResult result) {

            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.

                GoogleSignInAccount acct = result.getSignInAccount();
                Log.e(TAG, "display name: " + acct.getDisplayName());

                userdetail.setName(acct.getDisplayName());
                userdetail.setEmail(acct.getEmail());
                userdetail.setAppname("Google");
                userdetail.setLogintime(DateFormat.getDateTimeInstance().format(new Date()));

                String personName = acct.getDisplayName();
                String email = acct.getEmail();

                userName.setText(personName);
                userEmail.setText(email);

                String Pic = "";
                if (acct.getPhotoUrl() == null) {
                    userPic.setImageResource(R.drawable.defaultpic);
                } else {
                    Pic = acct.getPhotoUrl().toString();
                    Glide.with(getApplicationContext()).load(Pic)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(userPic);
                    Toast.makeText(this, "You have successfully logged in", Toast.LENGTH_SHORT).show();
                }
                dbhelper.insertRecord(userdetail);

                updateUI(true);


                Log.e(TAG, "Name: " + personName + ", email: " + email
                        + ", Image: " + Pic);

            }
            else{
                    // Signed out, show unauthenticated UI.
                    updateUI(false);
                }
            }

    public void openFileManager() {
      Intent imagePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//Intent.ACTION_OPEN_DOCUMENT
      imagePicker.addCategory(Intent.CATEGORY_OPENABLE);
        imagePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
       imagePicker.setAction(Intent.ACTION_GET_CONTENT);
//        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator);
//        String imagepath=file.getPath();
//        Uri imageUri= Uri.parse(imagepath);
//        imagePicker.setType("image/*");
//        startActivityForResult(Intent.createChooser(imagePicker,"Select Picture"),PICK_MULTIPLE_IMAGE);
//
//        InputStream  inputStream;
//        try {
//            Uri data=imageUri.parse(imagepath);
//            inputStream= getContentResolver().openInputStream(imageUri);
//            Bitmap finalimage= BitmapFactory.decodeStream(inputStream);
//            seeImage.setImageBitmap(finalimage);
//        }
//        catch (FileNotFoundException ex)
//        {
//            ex.printStackTrace();
//            Toast.makeText(this,"Picture not available",Toast.LENGTH_SHORT).show();
//
//        }

    }




    public  void updateUI(boolean isSignedIn) {

        if (isSignedIn) {
            gmailSignInButton.setVisibility(View.GONE);
            loginStatus.setText("Status");
            gmailSignOutButton.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            userEmail.setVisibility(View.VISIBLE);
            userPic.setVisibility(View.VISIBLE);
            fb_signinButton.setVisibility(View.GONE);
            optionText.setVisibility(View.GONE);
            filemanagerbutton.setVisibility(View.VISIBLE);

        }
        else {
            gmailSignOutButton.setVisibility(View.GONE);
            gmailSignInButton.setVisibility(View.VISIBLE);
            loginStatus.setVisibility(View.VISIBLE);
            userName.setVisibility(View.GONE);
            userEmail.setVisibility(View.GONE);
            userPic.setVisibility(View.GONE);
            fb_signinButton.setVisibility(View.VISIBLE);
            optionText.setVisibility(View.VISIBLE);
        }


    }


    public void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());
                            gmailSignOutButton.setVisibility(View.GONE);
                            gmailSignInButton.setVisibility(View.GONE);
                            fb_signinButton.setVisibility(View.GONE);
                            optionText.setVisibility(View.GONE);
                            fb_signoutButton.setVisibility(View.VISIBLE);

                            String email = response.getJSONObject().getString("email");
                            Profile profile = Profile.getCurrentProfile();
                            String link = profile.getLinkUri().toString();
                            String name = profile.getName();


                            userdetail.setName(profile.getName());
                            userdetail.setEmail(response.getJSONObject().getString("email"));
                            userdetail.setAppname("Facebook");
                            userdetail.setLogintime(DateFormat.getDateTimeInstance().format(new Date()));
                            loginStatus.setText("Login Success");

                            userName.setText(name);
                            userEmail.setText(email);
                            userPic.setVisibility(View.VISIBLE);
                            userName.setVisibility(View.VISIBLE);
                            userEmail.setVisibility(View.VISIBLE);

                            loginStatus.setVisibility(View.VISIBLE);
                            Log.i("Link", link);
                            if (Profile.getCurrentProfile() != null) {

                                Glide.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200))
                                        .thumbnail(0.5f)
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(userPic);

                            }
                            dbhelper.insertRecord(userdetail);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }


    public void fblouout() {

        LoginManager.getInstance().logOut();
        dbhelper.delete();
        gmailSignOutButton.setVisibility(View.GONE);
        gmailSignInButton.setVisibility(View.VISIBLE);
        fb_signinButton.setVisibility(View.VISIBLE);
        fb_signoutButton.setVisibility(View.GONE);
        loginStatus.setVisibility(View.VISIBLE);
        userPic.setVisibility(View.GONE);
        userEmail.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        optionText.setVisibility(View.VISIBLE);
        loginStatus.setText("Status");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (callbackManager.onActivityResult(requestCode, resultCode, data))
        {
            return;
        }
            if(requestCode==PICK_MULTIPLE_IMAGE)
            {
                Intent FileManagerClass=new Intent(getApplicationContext(),FileManagerActivity.class);
                startActivity(FileManagerClass);
                //Intent imagePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //imagePicker.addCategory(Intent.CATEGORY_OPENABLE);
                //imagePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //imagePicker.setAction(Intent.ACTION_GET_CONTENT);
                File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator);
                String imagepath=file.getPath();
                //Uri imageUri= Uri.parse(imagepath);
               // imagePicker.setDataAndType(imageUri,"image/*");
                try {
                   Uri image= data.getData();
                    InputStream inputStream= getContentResolver().openInputStream(image);
                    Bitmap finalimage= BitmapFactory.decodeStream(inputStream);
                    seeImage.setImageBitmap(finalimage);
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(this,"Picture not available",Toast.LENGTH_SHORT).show();

                }

            }
        }



    @Override
    public void onClick(View v) {
        int id = v.getId();

            switch (id) {

                case R.id.gmail_signinbutton:
                    signIn();
                    break;
                case R.id.gmail_signoutbutton:
                    signOut();
                    break;
                case R.id.fb_signinbutton:
                    LoginManager.getInstance().logInWithReadPermissions(GooglePlusSignIn.this
                            , Arrays.asList("public_profile", "user_friends", "email"));
                    break;
                case R.id.fb_signoutbutton:
                    fblouout();
                    break;
                case R.id.filemangaerbutton:
                    openFileManager();
                    break;

            }




    }


}

