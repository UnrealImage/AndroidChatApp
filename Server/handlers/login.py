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
class LoginHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			user_id = self.get_argument('user_id')
			password = self.get_argument('password')
			if database.checkuser(user_id, password):
				user_info = database.GetUserById(user_id)
				self.set_status(200)
				self.res_status['msg'] = 'login success'
				self.res_status['user_name'] = user_info[0]['user_name']
				self.res_status['config'] = user_info[0]['config']
				self.write(json.dumps(self.res_status))
				print self.res_status
				self.finish()
				return
			else:
				self.set_status(403)
				self.res_status['msg'] = 'illegal username or password'
				self.write(json.dumps(self.res_status))
				self.finish()
				print self.res_status
				return
		except Exception as error:
			print error
			self.res_status['msg'] = str(error)
			self.write(json.dumps(self.res_status))
			self.set_status(404)
			self.finish()

	def get(self):
		self.write("login test get")
		self.set_status(200)
		self.finish()