project = 'AnaMorf'
author = 'David Barry'
release = '4.0.0'

extensions = ['myst_parser']

html_theme = 'sphinx_rtd_theme'

source_suffix = {'.rst': 'restructuredtext', '.md': 'markdown'}

myst_heading_anchors = 3
myst_enable_extensions = ['colon_fence', 'deflist']

exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']
