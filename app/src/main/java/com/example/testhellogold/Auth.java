package com.example.testhellogold;

public class Auth {

    public static User user = null;

    public static class User {
        private String userEmail;
        private String api_token;
        private String account_number;
        private String public_key;
        private String api_key;

        public User(String userEmail,String api_token, String account_number, String public_key, String api_key) {
            this.userEmail = userEmail;
            this.api_token = api_token;
            this.account_number = account_number;
            this.public_key = public_key;
            this.api_key = api_key;
        }

        public String getUserEmail() { return userEmail; }

        public String getApi_token() {
            return api_token;
        }

        public String getAccount_number() {
            return account_number;
        }
    }
}
