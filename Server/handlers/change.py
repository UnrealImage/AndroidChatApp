# !/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

from base import BaseHandler
from logconfig import logconfig
import mysqlDb.mysqlDb_helper as database
import json

# noinspection PyAbstractClass
class ChangeHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			if self.check_user():
				user_name = self.get_argument('user_name')
				new_password = self.get_argument('new_password')
				config = self.get_argument('config')
				self.set_status(200)
				self.res_status['msg'] = 'success' 
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