<template>
  <div class="container homepage-container">
    <div class="home-nav row">
      <div class="home-nav-logo col-3"><br/></div>
    </div>
    <div class="row banner-image">
      <div class="banner-content">
        <h1>Sign Up!</h1>
        <h4>Hey. Click Here to make an account. </h4>
      </div>
    </div>
    <br/>

    <section class="feature-area section-gap" id="feature">
      <div class="container">
        <div class="row">
          <div class="single-feature col-lg-4">
            <img class="img-fluid" src="https://colorlib.com/preview/theme/callcenter/img/f1.png" alt="">
            <h4>Online Support</h4>
            <p>Lorem ipsum dolor sit amet, consecteturadipis icing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
          </div>
          <div class="single-feature col-lg-4">
            <img class="img-fluid" src="https://colorlib.com/preview/theme/callcenter/img/f2.png" alt="">
            <h4>24/7 Round the Clock</h4>
            <p>Lorem ipsum dolor sit amet, consecteturadipis icing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
          </div>
          <div class="single-feature col-lg-4">
            <img class="img-fluid" src="https://colorlib.com/preview/theme/callcenter/img/f2.png" alt="">
            <h4>Handling All Issues</h4>
            <p>Lorem ipsum dolor sit amet, consecteturadipis icing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>
<script>
  export default {
    name: "home"
  }
</script>
<style scoped="true" lang="css">
  body {
    margin: 0 !important;
  }

  .homepage-container {
    margin-left: 0;
    margin-right: 0;
    max-width: none;
  }

  .home-nav {
    position: fixed;

  }

  .home-nav-logo {
    background-image: url('/static/images/logo.png');
  }

  .banner-image {
    background-size: cover;
    background-image: url('/static/images/banner.jpg');
    min-height: 450px;
  }

  .banner-content {
    color: white;
    background: rgb(255,255,255);
    background: linear-gradient(0deg, rgba(255,255,255,0.44861694677871145) 0%, rgba(79,79,222,0.9023984593837535) 5%, rgba(152,238,255,1) 100%);
    padding: 20px;
    margin: 10vh 16vw auto auto;
    border-radius: 15px;

    animation: slideInFromLeft 700ms;
  }

  @keyframes slideInFromLeft {
    from {
      margin-right: -1000px;
    }
    to {
      margin-right: 16vw;
    }
  }

</style>
