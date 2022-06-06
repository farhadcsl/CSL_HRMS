<ul id="tabs" class="ui-helper-clearfix tabs">  
  <li class="active"><a href="#tab1">Company</a></li>
  <li class=""><a href="#tab2">Division</a></li>
  <li class=""><a href="#tab3">Department</a></li>
  <li class=""><a href="#tab4">Section</a></li>
  <li class=""><a href="#tab5">Line</a></li>
</ul>
<div id="panels">
 
   <form method="post" id="tab1"  name="movePositionFromCompany"  action="<@ofbizUrl>movePositionFromCompany</@ofbizUrl>" style="display: block;" class="tab dirtybit saver">
        <label>Organization to move:</label>
        	<select name="age">
        		<option>10</option>
        		<option>20</option>
        		<option>30</option>
        		<option>40</option>
        		<option>50</option>
        		<option>60</option>
        		<option>70</option>
        		<option>80</option>
        		<option>90</option>
        		<option>100</option>
        	</select>
        	<br/><br/>
        <input type="submit" value="submit">
  </form>
  <form id="tab2" class="tab dirtybit saver" style="display: none;">
      <label>Organization to move:</label>
        	<select name="age">
        		<option>10</option>
        		<option>20</option>
        		<option>30</option>
        		<option>40</option>
        		<option>50</option>
        		<option>60</option>
        		<option>70</option>
        		<option>80</option>
        		<option>90</option>
        		<option>100</option>
        	</select>
        	<br/><br/>
        <input type="submit" value="submit">
  </form>
  <form id="tab3" class="tab dirtybit saver" style="display: none;">
      <label>Organization to move:</label>
        	<select name="age">
        		<option>10</option>
        		<option>20</option>
        		<option>30</option>
        		<option>40</option>
        		<option>50</option>
        		<option>60</option>
        		<option>70</option>
        		<option>80</option>
        		<option>90</option>
        		<option>100</option>
        	</select>
        	<br/><br/>
        <input type="submit" value="submit">
  </form>
  <form id="tab4" class="tab dirtybit saver" style="display: none;">
      <label>Organization to move:</label>
        	<select name="age">
        		<option>10</option>
        		<option>20</option>
        		<option>30</option>
        		<option>40</option>
        		<option>50</option>
        		<option>60</option>
        		<option>70</option>
        		<option>80</option>
        		<option>90</option>
        		<option>100</option>
        	</select>
        	<br/><br/>
        <input type="submit" value="submit">
  </form>
  <form id="tab5" class="tab dirtybit saver" style="display: none;">
      <label>Organization to move:</label>
        	<select name="age">
        		<option>10</option>
        		<option>20</option>
        		<option>30</option>
        		<option>40</option>
        		<option>50</option>
        		<option>60</option>
        		<option>70</option>
        		<option>80</option>
        		<option>90</option>
        		<option>100</option>
        	</select>
        	<br/><br/>
        <input type="submit" value="submit">
  </form>
</div>
<div id="modal">
    <div id="background"></div>
    <div id="box"><p>Do you wish to save?</p>
         <a href="javascript://" id="yes">Yes</a>
         <a href="javascript://" id="no">No</a>
         <a href="javascript://" id="cancel">Cancel</a>
    </div>
</div>


<script type="text/javascript">
can.Control("Tabs", {
  init : function (el) {
    $(el).children("li:first").addClass('active');
    var tab = this.tab;
    this.element.children("li:gt(0)").each(function () {
      tab($(this)).hide()
    })
  },
  tab : function (li) {
    return $(li.find("a").attr("href").match(/#.*/)[0])
  },
  "li click" : function (el, ev) {
    ev.preventDefault();
    var active = this.element.find('.active')
    old = this.tab(active),
        cur = this.tab(el);
    old.triggerAsync('hide', function () {
      active.removeClass('active')
      old.slideUp(function () {
        el.addClass('active')
        cur.slideDown()
      });
    })
  }
})

// create a widget listens for change and marks as dirty
can.Control("Dirtybit", {
  init : function () {
    this.element.data('formData', this.element.serialize())
  },
  "change" : function (el) {
    this.check()
  },
  keyup : function (el) {
    this.check()
  },
  click : function (el) {
    this.check()
  },
  check : function () {
    var el = this.element;
    if (el.serialize() == el.data('formData')) {
      el.removeClass('dirty')
    } else {
      el.addClass('dirty')
    }
  },
  " set" : function () {
    this.element.data('formData', this.element.serialize())
        .removeClass('dirty');
  }
})

// a modal width
can.Control("Modal", {
  init : function () {
    this.element.show();
  },
  "a click" : function (a) {
    this.element.hide();
    this.options[a.attr('id')]();

    this.destroy();
  }
})

// create a saver widget
can.Control("Saver", {
  " hide" : function (el, ev) {
    if (el.hasClass('dirty')) {
      ev.pause()
      new Modal('#modal', {
        yes : function () {
          var save = $('<span>Saving</span>').appendTo(el);
          $.post("/update", el.serialize(), function () {
            save.remove();
            el.trigger('set');
            ev.resume();
          })
        },
        no : function () {
          ev.resume();
        },
        cancel : function () {
          ev.preventDefault();
          ev.resume();
        }
      })
    }
  }
});

new Tabs('#tabs');
$('#panels .tab').each(function() {
    new Dirtybit(this);
    new Saver(this);
});

// a fake post method
$.post = function (url, data, success) {
  setTimeout(success, 500)
};
</script>

<style>
.tabs {
  padding: 0px;
  margin: 0px;
}

.tabs li {
    background-color: #F6F6F6;
    float: left;
    list-style: none outside none;
    margin-left: 1px;
    padding: 10px;
}

.tabs li a {
  color: #1C94C4;
  font-weight: bold;
  text-decoration: none;
    }

.tabs li.active a {
  color: #F6A828;
  cursor: default;
}

.tab {
    background: none repeat scroll 0 0 #F6F6F6;
    border: 2px solid #F6F6F6;
    height: 100px;
    margin: 1px 0 0 1px;
    padding: 20px;
    width: 360px;
}

/*.dirty {
  border: solid 2px red;
}*/

  /* clearfix from jQueryUI */
.ui-helper-clearfix:after {
  content: ".";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
    }

.ui-helper-clearfix {
  display: inline-block;
    }

  /* required comment for clearfix to work in Opera \*/
* html .ui-helper-clearfix {
  height: 1%;
}

.ui-helper-clearfix {
  display: block;
}

  /* end clearfix */
#modal {
  position: absolute;
  top: 0px;
  left: 0px;
  height: 100%;
  width: 100%;
  display: none;
}

#background {
  background-color: gray;
  top: 0px;
  left: 0px;
  height: 100%;
  width: 100%;
  opacity: 0.5;
}

#box {
  position: absolute;
  margin: -55px 0px 0px -105px;
  top: 50%;
  left: 50%;
  border: solid 1px gray;
  background-color: white;
  opacity: 1;
  width: 200px;
  height: 100px;
  padding: 10px;
}

/*span {
  color: Red;
}*/

label {
  display: block;
}
</style>

