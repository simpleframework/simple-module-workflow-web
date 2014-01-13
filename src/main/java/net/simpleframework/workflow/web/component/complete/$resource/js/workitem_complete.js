var WF_WORKITEM_COMPLETE_ACTION = {

	transitionSave : function(obj, params) {
		var c = obj.up(".transition_manual");
		var id = "";
		c.select(".node input[type=checkbox]").each(function(box) {
			if (box.checked) {
				id += ";" + box.value;
			}
		});
		if (id.length > 0) {
			$Actions['ajaxTransitionManualSave'](params + '&transitions='
					+ id.substring(1));
		} else {
			$UI.shakeMsg(c.down(".msg"), this.TRANSITION_MESSAGE);
		}
	},

	participantSave : function(obj, params) {
		var c = obj.up(".participant_manual");
		var data = [];
		c.select(".node").each(function(p) {
			var o = {
				"transition" : p.readAttribute("transition")
			};
			data.push(o);
			o.participant_obj = p.next();
			var id = "";
			o.participant_obj.select("input[type=checkbox]").each(function(box) {
				if (box.checked) {
					id += ";" + box.value;
				}
			});
			if (id.length > 0) {
				o.participant = id.substring(1);
			}
		});
		if (data.any(function(o) {
			if (!o.participant) {
				$UI.shakeMsg(o.participant_obj.down(".msg"), this.PARTICIPANT_MESSAGE);
				return true;
			}
			delete o.participant_obj;
		}.bind(this))) {
			return;
		}
		$Actions["ajaxParticipantManualSave"](params + '&json=' + 
				encodeURIComponent(Object.toJSON(data)));
	}
};