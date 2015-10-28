$ready(function() {
  $Elements(".MyQueryWorksTPage .col1 .gitem").each(
      function(item) {
        var psub = item.down(".psub");
        item.observe("mouseover", function(ev) {
          var p = item.cumulativeOffset();
          psub.setStyle("top: " + p.top + "px; left: "
              + (p.left + item.getWidth() - 1) + "px;");
          psub.show();
        });
        psub.observe("mouseleave", function(ev) {
          psub.hide();
        });
        item.observe("mouseleave", function(ev) {
          psub.hide();
        });
      });
});