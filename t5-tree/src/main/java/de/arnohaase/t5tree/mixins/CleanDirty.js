
(function ($) {
  var methods = {
    track: function (dirtyClass, cleanClass) {
      this.each(function() {
        var $el = $(this);
        $el.data('cleanValue', $el.val());
        $el.change(function() {
          if($el.val() === $el.data('cleanValue')) $el.removeClass(dirtyClass); else $el.addClass(dirtyClass);
          if (cleanClass) {
            if($el.val() === $el.data('cleanValue')) $el.addClass(cleanClass); else $el.removeClass(cleanClass);
          }
        });
        $el.change();
      });
    },
    reset: function (selector) {
      this.each(function() {
    	var oldId = this.id;
    	if (! oldId) {
          this.id = '__cLean__dIrty__tEmporary__iD__';
    	}
    	
        $('#' + this.id + ' ' + selector).each(function() {
          var $el=$(this);
          $el.val($el.data('cleanValue'));
          $el.change();
        });
    	
    	this.id = oldId;
      });
    },
    isDirty: function(selector) {
      var result = false;

      this.each(function() {
        var oldId = this.id;
        if (! oldId) {
          this.id = '__cLean__dIrty__tEmporary__iD__';
        }

        $('#' + this.id + ' ' + selector).each(function() {
          var $el=$(this);
          
          if ($el.data('cleanValue') && $el.val() != $el.data('cleanValue')) {
        	result = true;
          }
        });

        this.id = oldId;
      });

      return result;
    }
  };

  $.fn.cleanDirty = function (method) {
    // Method calling logic
    if ( methods[method] ) {
      return methods [method].apply (this, Array.prototype.slice.call (arguments, 1));
    } 
    else {
      $.error( 'Method ' +  method + ' does not exist on jQuery.cleanDirty' );
    }    

  };
	
})(jQuery);


