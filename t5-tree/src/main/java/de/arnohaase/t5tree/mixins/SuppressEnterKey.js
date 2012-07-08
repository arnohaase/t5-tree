(function ($) {
  $.fn.suppressEnterKey = function () {
    return this.keydown(function(event) {
      var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
      if (keyCode == 13) {
        return false;
      }
      return true;
    });
  };
})(jQuery);
