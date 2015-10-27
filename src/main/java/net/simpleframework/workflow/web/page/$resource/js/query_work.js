$ready(function() {
  $Elements(".MyQueryWorksTPage .col1 .gitem").each(function(item) {
    var psub = item.down(".psub");
    item.observe("mouseover", function(ev) {
      var top = item.cumulativeOffset()[1];
      psub.setStyle("top: " + (top) + "px");
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