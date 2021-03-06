// src/auth/index.js

import axios from 'axios'
import store from './store'

// URL and endpoint constants

const LOGIN_URL = window.leadlucky.apiUrl + 'login'
const SIGNUP_URL = window.leadlucky.apiUrl + 'users/signup'

export default {


  // Send a request to the login URL and save the returned JWT
  login(username, password, callback) {
    axios.post(LOGIN_URL, {username, password}).then(res => {
      localStorage.setItem('id_token', res.data);
      localStorage.setItem('access_token', res.data);

      callback(true)
    }).catch(error => {
      callback(
        false,
        (error.response && error.response.data && error.response.data.message)
        || (error.response.status === 401 && "Invalid login attempt. ")
        || error.message
      )
    });
  },

  signup(username, password, email, callback) {
    axios.post(SIGNUP_URL, {username, password, email}, {headers: {"Content-Type": "application/json"}}).then(res => {
      localStorage.setItem('id_token', res.data);
      localStorage.setItem('access_token', res.data);

      callback(true)
    }).catch(error => {
      callback(
        false,
        (error.response && error.response.data && error.response.data.message)
        || error.message
      )
    });
  },

  // To log out, we just need to remove the token
  logout() {
    localStorage.removeItem('id_token');
    localStorage.removeItem('access_token');
    store.userData.user = null;
  },

  refreshAuth(callback) {
    callback = callback || function(){};
    const jwt = localStorage.getItem('id_token');
    if(jwt) {
      axios.get(window.leadlucky.apiUrl + 'users/me', {headers: this.getAuthHeader()}).then((res) => {
        store.userData.user = res.data;
        callback(store.userData.user)
      }).catch((err) => {
        store.userData.user = null;
        console.error(err);
        callback(null)
      })
    }
    else {
      store.userData.user = null;
      callback(null)
    }
  },

  // The object to be passed as a header for authenticated requests
  getAuthHeader() {
    return {
      "Authorization": "Bearer " + localStorage.getItem('access_token')
    }
  }
}
