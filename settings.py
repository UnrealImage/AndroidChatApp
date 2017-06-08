# !/usr/bin/env python
# -*- coding: utf-8 -*-

"""
@author: morc
@contact: 709403987ac@gmail.com
@create: 16/7/24
"""


import tornado
import tornado.template
from tornado.options import define, options
from logconfig import logconfig

import os

# Make file paths relative to settings.
get_path = lambda root, *a: os.path.join(root, *a)
ROOT = os.path.dirname(os.path.abspath(__file__))

define("port", default=9001, help="run on the given port", type=int)
define("config", default=None, help="tornado config file")
define("debug", default=False, help="debug mode")
tornado.options.parse_command_line()

# Deployment Configuration 部署配置
# settings 配置
settings = dict()
settings['debug'] = False
settings['cookie_secret'] = "your-cookie-secret"
settings['buf_size'] = 4096 

# database settings
settings['db_host'] = "localhost"
settings['db_database'] = "Android"
settings['db_user'] = 'root'
settings['db_password'] = '83147439'
settings["db_time_zone"] = '+8:00'
#settings['db_host'] = "localhost"
# settings['db_database'] = "MatrixFiie"
# settings['db_database_dev'] = 'matrix_file_dev'
# settings['db_user'] = 'matrix'
# settings['db_password'] = '123456'
# settings["db_time_zone"] = '+8:00'


# seaweedfs master(leader) address
#settings['master'] = 'http://127.0.0.1:9333'

#settings['auth_url'] = "https://127.0.0.1:5000/v1/api/verifyToken"

# log 配置
logconfig.initialize_logging()

if options.config:
    tornado.options.parse_config_file(options.config)
