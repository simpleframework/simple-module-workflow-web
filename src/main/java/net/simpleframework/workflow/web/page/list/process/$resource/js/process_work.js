$ready(function() {
  var _init_process_category_tree = function(cc) {
    cc = $(cc);
    var gtree = cc.down(".gtree");
    var w = gtree.getWidth();
    var h = gtree.getHeight();

    var hideLast = function() {
      var last = gtree._last;
      if (last) {
        last.removeClassName("active");
        last.down(".psub").removeClassName("show");
      }
    };

    gtree.observe("mouseleave", function(ev) {
      hideLast();
    });

    var _item = cc.down(".gitem");
    var p = _item.cumulativeOffset();
    cc.select(".gitem").each(
        function(item) {
          var psub = item.down(".psub").setStyle(
              "top: " + p.top + "px; left: " + (p.left + _item.getWidth()) + "px;");
          item.observe("mouseenter", function(ev) {
            item._enter = true;
            (function() {
              if (!item._enter)
                return;
              hideLast();
              item.addClassName("active");
              psub.addClassName("show");
              gtree._last = item;
            }).delay(0.1);
          }).observe("mouseleave", function(ev) {
            item._enter = false;
          });
          psub.observe("mouseleave", function(ev) {
            var last = gtree._last;
            if (last == item)
              return;
            item.removeClassName("active");
            psub.removeClassName("show");
          });
        });
  };

  _init_process_category_tree(".MyProcessWorksTPage .col1");
});