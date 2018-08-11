<template>
  <div class="card">
    <div class="media">
      <div class="media-left meida media-middle">
        <span><i :class="`fa fa-${icon} f-s-40 color-${color}`"></i></span>
      </div>
      <div class="media-body media-text-right">
        <h2>{{value}}</h2>
        <p class="m-b-0">{{name}}</p>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'info-card',
    props: {
      icon: {
        type: String,
        default: null
      },
      color: {
        type: String,
        default: 'primary'
      },
      value: {
        default: ''
      },
      name: {
        type: String,
        default: ''
      }
    }
  }
</script>
