$ready(function() {
  $Elements(".MyQueryWorksTPage .col1 .gitem").each(
      function(item) {
        var psub = item.down(".psub");
        var psep = item.down(".psep");
        item.observe("mouseover", function(ev) {
          var p = item.cumulativeOffset();
          psub.setStyle("top: " + p.top + "px; left: "
              + (p.left + item.getWidth() - 1) + "px;");
          psep.setStyle("height: " + (item.getHeight() - 2)  + "px");
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