# !/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding("utf-8")

from base import BaseHandler
from logconfig import logconfig
import mysqlDb.mysqlDb_helper as database

# noinspection PyAbstractClass
class FooHandler(BaseHandler):
    def get(self):
        self.write('test get')
    def post(self):
    	self.write('test post')
    def put(self):
    	self.write(' test put ')
