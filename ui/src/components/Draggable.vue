<template>
  <div ref="dragElement" class="draggable" :style="initialStyle">
      <div @mousedown="beginDrag" style="background: #f0f0f0; border-top-left-radius: 5px; border-top-right-radius: 5px; margin: 0">
        <br/>
      </div>
    <div style="margin: 0; border-bottom-left-radius: 5px; border-bottom-right-radius: 5px; background: white;"><slot name="content"/></div>

  </div>
</template>

<script>
  const pxToNumber = (val) => Number(String(val).replace("px", ""));

  export default {
    name: "draggable",
    props: {
      frames: {
        type: Array,
        default: () => []
      },
      initialLeft: {
        type: Number,
        default: 0
      },
      initialTop: {
        type: Number,
        default: 0
      },
      initialRight: {
        type: Number,
        default: 0
      },
      width: {
        type: Number,
        default: 0
      },
      minX: {
        type: Number,
        default: 0
      },
      minY: {
        type: Number,
        default: 0
      }
    },
    data: () => ({
      initialized: false,
    }),
    computed: {
      initialStyle() {
        if (this.initialized) {
          return ''
        }
        let style = '';

        let width = 0;
        let left = 0;
        let top = 0;

        if (this.width) {
          width = pxToNumber(this.width)
        }

        if (this.initialRight) {
          left = (window.innerWidth - width - pxToNumber(this.initialRight))
        } else if (this.initialLeft) {
          left = pxToNumber(this.initialLeft)
        }

        if (this.initialTop) {
          top = Number(String(this.initialTop).replace('px', ''))
        }

        style += `left: ${left}px;`;
        style += `top: ${top}px;`;
        style += `width: ${width}px;`;

        this.initialized = true;

        return style;
      }
    },
    methods: {
      pxToNumber(val) {
        return Number(String(val).replace("px", ""))
      },
      beginDrag(mouseDownEvent) {
        const el = this.$refs.dragElement;

        let mouseX = mouseDownEvent.clientX;
        let mouseY = mouseDownEvent.clientY;
        let offsetLeft = pxToNumber(el.style.left) - mouseX;
        let offsetTop = pxToNumber(el.style.top) - mouseY;

        viewport.onmousemove = (e) => {
          mouseX = e.clientX;
          mouseY = e.clientY;



          el.style.left = (mouseX + offsetLeft) + "px";
          el.style.top = (mouseY + offsetTop) + "px";
        };

        viewport.onmouseup = () => {
          window.onmouseup = null;
          window.onmousemove = null;
          el.style.top = Math.max(pxToNumber(el.style.top), this.minY) + "px";
          el.style.left =
            pxToNumber(el.style.width) * .5 + pxToNumber(el.style.left) < this.minX ?
            this.minX - (pxToNumber(el.style.width) * .5) : el.style.left
        };
      }
    }
  }
</script>

<style scoped>
  .draggable {
    position: fixed;
    width: 25%;
    top: 96px;
    left: calc(100vw - 16px - 25%);
  }
</style>
