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
# yes
class GetmsgHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			if self.check_user():
				to_user = self.get_argument("user_id")
				contact_id = self.get_argument('contact_id')
				msg = database.GetMsg(msg_id, to_user)
				self.set_status(200)
				self.res_status['msg'] = 'success'
				self.res_status['msg_id'] = []
				self.write(json.dumps(self.res_status))
				self.finish()

				#generate new msg, type 3 readed
				new_msg = {}
				new_msg['type'] = 3
				new_msg['from_user'] = to_user
				new_msg['to_user'] = msg['from_user']
				new_msg['msg'] = msg['msg_id']
				
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