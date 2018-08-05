<template>
  <v-app id="inspire">
    <v-content class="background">
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card class="elevation-12" style="background: rgba(255,255,255,0.6);" v-if="loggingIn">
              <v-card-text>
                <h1 class="fancy">Welcome</h1>
                <v-form v-model="loginValid">
                  <v-text-field
                    v-model="credentials.username"
                    v-on:keyup.enter="submit"
                    prepend-icon="person"
                    name="login"
                    label="Login"
                    type="text"
                    :rules="userIdRules"/>
                  <v-text-field
                    v-model="credentials.password"
                    v-on:keyup.enter="submit"
                    prepend-icon="lock"
                    name="password"
                    label="Password"
                    id="password"
                    type="password"
                    :rules="required('Password')"/>
                </v-form>
              </v-card-text>
              <p class="text-xs-left" v-text="errorLogin" style="color: red; padding-left: 10px"></p>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="transparent" @click="showRegistration()">Register</v-btn>
                <v-btn color="primary" @click="submit" :disabled="!loginValid">Login</v-btn>
              </v-card-actions>
            </v-card>
            <v-card class="elevation-12" style="background: rgba(255,255,255,0.6);" v-else>
              <v-card-text>
                <h1 class="fancy">Register</h1>
                <v-form v-model="registrationValid">
                  <v-text-field
                    v-model="credentials.username"
                    prepend-icon="person"
                    name="login"
                    label="Login"
                    type="text"
                    :rules="userIdRules"/>
                  <v-text-field
                    v-model="credentials.postEmail"
                    prepend-icon="email"
                    name="email"
                    label="Email"
                    type="text"
                    :rules="emailRules"/>
                  <v-text-field
                    v-model="credentials.password"
                    prepend-icon="lock"
                    name="password"
                    label="Password"
                    id="regpassword"
                    type="password"
                    :rules="passwordRules"/>
                  <v-text-field
                    v-model="credentials.passwordVerification"
                    prepend-icon="lock"
                    name="password"
                    label="Repeat Password"
                    :rules="passwordsMatch(credentials.password)"
                    id="regpassword2"
                    type="password"/>
                  <v-checkbox
                    v-model="checkbox"
                    :rules="checkedRule('You must agree to continue.')"
                    required
                    color="primary">
                    <span slot="label" style="font-size: 0.75vw">Do you agree to the <a href="/#/legal" target="_blank">Terms & Conditions</a>and<a href="/#/privacy" target="_blank"> Privacy Policy</a>?</span>
                  </v-checkbox>
                </v-form>
              </v-card-text>
              <p class="text-xs-left" v-text="errorText" style="color: red; padding-left: 10px"></p>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" @click="register" :disabled="!registrationValid">Register</v-btn>
                <v-btn color="transparent" @click="showLogin()">Login</v-btn>
              </v-card-actions>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
  import auth from '../../auth'
  import {validationMixin} from '@leadlucky/leadlucky-themes'

  export default {
    mixins: [validationMixin],
    data: () => ({
      loginValid: false,
      registrationValid: false,
      loggingIn: true,
      errorText: '',
      checkbox: false,
      errorLogin: '',
      credentials: {
        username: '',
        password: '',
        passwordVerification: '',
        postEmail: ''
      }
    }),
    props: {
      source: String
    },
    computed: {
      referName() {
        const referrer = this.$route.params.referrer;

        if(referrer != null){
          const clean = referrer.replace(/[^a-z0-9]/gi,'');
          return clean
        }

        return referrer
      }
    },
    methods: {
      submit() {
        const self = this;
        self.errorLogin = null;
        let data = JSON.stringify({
          username: this.credentials.username,
          password: this.credentials.password
        });

        auth.login(data, (success, message) =>{
          if(!success){
            self.errorLogin = message || 'Incorrect username or password.'
          }
        })
      },
      register() {
        this.errorText = null;
        const self = this;
          let data = JSON.stringify({
            username: this.credentials.username,
            password: this.credentials.password,
            email: this.credentials.postEmail,
            referrer: self.referName
          });

          auth.signup(data, (success, message) =>{
            if(!success){
              self.errorText = message || 'Username or Email already exists.'
            }
          })
      },
      showLogin(){
        this.loggingIn = true
      },
      showRegistration(){
        this.loggingIn = false
      },
    },
    created(){
      const router = this.$router
      auth.refreshAuth((user) => {
        if(user){
          router.push('/dashboard')
        }
      })
    }
  }
</script>

<style>
  .background {
    background-image: url('/static/background.jpeg');
    background-repeat: no-repeat;
    background-size: 100% 100%;
  }
  .fancy{
    font-family: Arial, Helvetica, sans-serif;
    font-size: 40px;
  }

</style>
