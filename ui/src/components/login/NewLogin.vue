<template>
  <div id="main-wrapper">
    <link href="/static/css/style.css" rel="stylesheet">
    <div class="unix-login">
      <div class="container-fluid">
        <div class="row justify-content-center">
          <div class="col-lg-4">
            <div class="login-content card">
              <div class="login-form">
                <h4>Sign {{register ? 'Up' : 'In'}}</h4>
                <div class="form-group">
                  <label>Username </label>
                  <ela-input
                    v-model="username.value"
                    :validationError="username.validator(username.value)"
                    type="text"
                    placeholder="Username "/>
                </div>
                <div class="form-group">
                  <label>Password </label>
                  <ela-input
                    v-model="password.value"
                    :validationError="password.validator(password.value)"
                    type="password"
                    placeholder="Password "/>
                </div>
                <div :class="`register-section${register ? '' : ' hidden-fields'}`">
                  <div class="form-group" v-if="register">
                    <label>Confirm Password </label>
                    <ela-input
                      v-model="passwordConfirm.value"
                      :validationError="passwordConfirm.validator(passwordConfirm.value)"
                      type="password"
                      placeholder="Password "/>
                  </div>
                  <div class="form-group" v-if="register">
                    <label>Email </label>
                    <ela-input
                      v-model="email.value"
                      :validationError="email.validator(email.value)"
                      type="email"
                      placeholder="Email "/>

                  </div>
                </div>
                <!--<label>-->
                <!--<input type="checkbox"> Remember Me-->
                <!--</label>-->
                <p :style="`color: ${error ? 'red' : 'black'}`">{{message}}</p>
                <button @click="act" class="btn btn-primary btn-flat m-b-30 m-t-30">Sign {{register ? 'Up' : 'In'}}
                </button>
                <div class="register-link m-t-15 text-center">
                  <p>{{register ? 'Already' : "Don't"}} have an account?<a href="#" @click="register=!register">
                    Sign {{register ? 'In' : 'Up'}} Here</a></p>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import auth from '../../auth'
  import {validationMixin} from '@leadlucky/leadlucky-themes'
  import ElaInput from '../ela/ElaInput.vue'


  export default {
    components: {ElaInput},
    mounted() {
      this.passwordConfirm.validator = (v) => v && v === this.password.value ? null : "Passwords must match"
    },
    data: function () {
      return {
        username: {value: "", validator: validationMixin.required('Username')},
        password: {value: "", validator: validationMixin.passwordRules},
        passwordConfirm: {value: "", validator: 'x'},
        email: {value: "", validator: validationMixin.emailRules},
        register: false,
        message: null,
        error: false
      }
    },
    methods: {
      act() {
        const errors = [this.username, this.password, ...(this.register ? [this.passwordConfirm, this.email] : [])]
          .map((f) => f.validator(f.value)).filter((e) => e);
        if (errors.length > 0) {
          this.message = errors.join(", ");
          this.error = true;
          return;
        }
        return this.register ? this.signUp() : this.signIn()
      },
      signIn() {
        this.message = null;
        auth.login(this.username.value, this.password.value, (success, message) => {
          this.message = message;
          this.error = !success
          if (success) this.$router.push('/dashboard')
        })
      },
      signUp() {
        this.message = null;
        this.error = false;
        auth.signup(this.username.value, this.password.value, this.email.value, (success, message) => {
          if (!success) {
            return this.message = message || 'Username or Email already exists.'
          }
          this.$router.push('/dashboard')
        })
      }
    },
    created() {
      const router = this.$router
      auth.refreshAuth((user) => {
        if (user) {
          router.push('/dashboard')
        }
      })
    }
  }
</script>
