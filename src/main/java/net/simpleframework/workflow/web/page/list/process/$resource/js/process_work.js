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

    var _item = cc;
    var p = _item.cumulativeOffset();
    cc.select(".gitem").each(
        function(item) {
          var _top = p.top - 15;
          var psub = item.down(".psub").setStyle(
              "top: " + _top + "px; left: " + (p.left + _item.getWidth() - 1) + "px;");
          item.observe("mouseenter", function(ev) {
            item._enter = true;
            (function() {
              if (!item._enter)
                return;
              hideLast();
              var l = item.cumulativeOffset().top + item.getHeight() - _top;
              if (l > psub.getHeight()) {
                psub.setStyle("height: " + (l -15) + "px");
              }
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