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
class RegisterHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			user_name = self.get_argument('user_name')
			password = self.get_argument('password')
			config = self.get_argument('config')
			print "register config:" + config.encode('utf-8')
			if False:
				print "none"
				self.set_status(403)
				self.res_status['msg'] = 'complit username'
				self.write(json.dumps(self.res_status))
				self.finish()
				return
			else:
				self.set_status(200)
				self.res_status['msg'] = 'success'
				user_id = database.GetNewUserId();
				self.res_status['user_id'] = user_id
				database.InsertUser(user_id, password, config.encode('utf-8'), user_name.encode('utf-8'))
				self.write(json.dumps(self.res_status))
				print self.res_status
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