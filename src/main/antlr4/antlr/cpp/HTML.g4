grammar HTML;

document: '<html>' header body EOF;

header: '<header>' content '</header>';
body: '<body>' content '</body>';

content: tag | TEXT;

TEXT: ~('<')*;