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
class GetContextHandler(BaseHandler):
	def initialize(self):
		self.res_status = {}
	
	def post(self):
		try:
			if self.check_user(): #check user illegal or not
				self.set_status(200)
				context = database.getContext(self.get_argument('user_id'))
				user_id = self.get_argument('user_id')

				self.res_status['user_id'] = []
				for each in context:
					if each['user_id_1'] == user_id:
						other_user = each['user_id_2']
						self.res_status['user_id'].append(each['user_id_2'])
					else:
						other_user = each['user_id_1']
						self.res_status['user_id'].append(each['user_id_1'])
					self.res_status[other_user] = {}
					other_user_detail = database.GetUserById(other_user)
					self.res_status[other_user]['user_name'] = other_user_detail['user_name']
					self.res_status[other_user]['config'] = other_user_detail['config']
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
		self.write("context test get")
		self.set_status(200)
		self.finish()