
var t5tree = {
  findParentCheckbox: function(el) {
    var childrenOfCls = $j.grep(el.className.split(' '), function(n) {
      return 0 === n.indexOf('child-of-');
    });
    if(childrenOfCls.length === 0) {
      return undefined;
    }
    
    var parentId = childrenOfCls[0].substr(9);
    var result = $j('#' + parentId).get(0);
    return result;
  },
  refreshCheckboxFromChildValues: function(el) {
	if (! el) {
      return;
	}
	  
	if ($j('.descendant-of-' + el.id).length === 0) {
	  console.log('no children for ' + el.id);
      return; // no child checkboxes --> do nothing
	}
	
	console.log('descendants of ' + el.id);
	console.log('  ' + $j('.descendant-of-' + el.id + ':checked').length);
	console.log('  ' + $j('.descendant-of-' + el.id + ':not(:checked)').length);
	console.log('  ' + $j('.descendant-of-' + el.id).length);
	
    if ($j('.descendant-of-' + el.id + ":checked").length === 0) {
      $j(el).prop('indeterminate', false).attr('checked', false);
    }
    else if ($j('.descendant-of-' + el.id + ":not(:checked)").length === 0) {
      $j(el).prop('indeterminate', false).attr('checked', true);
    }
    else {
      $j(el).prop('indeterminate', true).attr('checked', true);
    }
	
	var parent = t5tree.findParentCheckbox(el);
	if (parent) {
      t5tree.refreshCheckboxFromChildValues(parent);
	}
  },
  handleCheckBoxChange: function(el) {
    $j('.descendant-of-' + el.id).attr('checked', el.checked).prop('indeterminate', false);
    t5tree.refreshCheckboxFromChildValues(t5tree.findParentCheckbox(el));
  }
};

