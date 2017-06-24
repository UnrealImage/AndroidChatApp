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
class HeartHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			if self.check_user():
				self.set_status(200)
				user_id = self.get_argument('user_id')
				msg = database.GetUnsendedMsg(user_id)
				'''if len(msg) == 0: #empty return 403
					self.set_status(403)
					self.res_status['msg'] = "empty msg"
					self.write(json.dumps(self.res_status))
					self.finish()
					return'''
				self.res_status['new_msg'] = {}
				self.res_status['new_shake'] = ['000000']
				self.res_status['new_friend'] = {}
				self.res_status['new_readed'] = {}
				self.res_status['new_setting'] = {}
				self.res_status['msg'] = 'success'
				self.res_status['flag'] = ""
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
