<template>
  <div>
    <input
      :value="this.value || ''"
      @input="updateValue"
      class="form-control"
      :type="type"
      :placeholder="placeholder">
    <div v-if="validationError && hasInput"
         class="invalid-feedback is-invalid animated fadeInDown"
         style="display: block">
      {{validationError}}
    </div>
  </div>
</template>
<script>
  export default {
    name: 'ela-input',
    props: [
      'value', 'placeholder', 'type', 'validationError'
    ],
    data: () => ({hasInput: false}),
    methods: {
      updateValue(e){
        this.hasInput = true;
        this.$emit('input', e.target.value)
      }
    }
  }
</script>
