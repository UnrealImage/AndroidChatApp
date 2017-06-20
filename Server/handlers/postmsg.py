# !/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

from base import BaseHandler
from logconfig import logconfig
import mysqlDb.mysqlDb_helper as database
import json
import time

# noinspection PyAbstractClass
class PostmsgHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			if self.check_user():
				from_user = self.get_argument("user_id")
				to_user = self.get_argument('to_user')
				msg_type = self.get_argument('type')
				msg = self.get_argument('msg')
				now_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
				msg_id = database.InsertMsg(from_user, to_user, msg, now_time, msg_type)
				self.set_status(200)
				self.res_status['msg'] = 'success'
				self.res_status['msg_id'] = msg_id 
				self.write(json.dumps(self.res_status))
				self.finish()
				return
			else:
				self.res_status['msg'] = 'illegal userid or password'
				self.set_status(403)
				self.write(json.dumps(self.res_status))
				self.finish()
				return
		except Exception as error:
			print error
			self.res_status['msg'] = str(error)
			self.write(json.dumps(self.res_status))
			self.set_status(404)
			self.finish()

	def get(self):
		self.write("register test get")
		self.set_status(200)
		self.finish()