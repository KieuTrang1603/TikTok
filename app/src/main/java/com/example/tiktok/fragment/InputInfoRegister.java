package com.example.tiktok.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tiktok.R;
import com.example.tiktok.RegisterActivity;


public class InputInfoRegister extends Fragment {

    public final static String TAG = "InputInfoRegister";
    EditText mEditTextName, mEditTextPhone, mEditTextEmail;

    private static final InputInfoRegister instance = new InputInfoRegister();
    private InputInfoRegister() {
    }
    public static InputInfoRegister getInstance() {
        return instance;
    }

    // TODO: Rename and change types and number of parameters
    @Override
    //khởi tạo đầu vào
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    //thiết lập giao diện và fragment sẽ trở lại khi được gọi
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_info_register, container, false);

        // Binding
        Button btnContinue = view.findViewById(R.id.btn_continue_register);
        mEditTextName = view.findViewById(R.id.txt_fullname);
        mEditTextPhone = view.findViewById(R.id.txt_phonenumber);
        mEditTextEmail = view.findViewById(R.id.txt_email);

        btnContinue.setOnClickListener(v -> handleContinueButton());

        return view;
    }

    private void handleContinueButton() {
        String name = mEditTextName.getText().toString();
        String phoneNumber = mEditTextPhone.getText().toString();
        String email = mEditTextEmail.getText().toString();
        setData(name,phoneNumber,email);
        openInputAccountRegister();

//        if (isValidInputData(name, phonenumber, email)) {
//            setData(name, phonenumber, email);}
////
////            Query query = FirebaseUtil.getUserByEmail(email);
////            query.get().addOnCompleteListener(task -> {
////                if (task.isSuccessful()) {
////                    if (task.getResult().getChildrenCount() > 0)
////                        mEditTextEmail.setError("Email đã tồn tại");
////
//         else
//            openInputAccountRegister();
    }
    // Open InputAccountRegister fragment
    private void openInputAccountRegister() {
        RegisterActivity registerActivity = (RegisterActivity) requireActivity();
        registerActivity.openInputAccountRegister();
    }

    // Check input data
    public boolean isValidInputData(String name, String phoneNumber, String email) {
        if (email.isEmpty()) {
            mEditTextEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (name.isEmpty()) {
            mEditTextName.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            mEditTextPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (!isPhoneNumberValid(phoneNumber)) {
            mEditTextPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        return true;
    }

    // is phone number valid
    private boolean isPhoneNumberValid(String phoneNumber) {
        phoneNumber = phoneNumber.trim().replace("+84", "0");
        return phoneNumber.length() == 10 && phoneNumber.startsWith("0");
    }

    // set data for new user in registerActivity
    public void setData(String name, String phoneNumber, String email) {
        RegisterActivity registerActivity = (RegisterActivity) requireActivity();
        registerActivity.newUser.setFullName(name);
        registerActivity.newUser.setPhoneNumber(phoneNumber);
        registerActivity.newUser.setEmail(email);
    }

}