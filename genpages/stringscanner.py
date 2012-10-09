import re

class StringScanner:
    def __init__ (self, string):
        self._string = string
        self._pos = 0

    def scan (self, pattern, flags = 0):
        '''Tries to match 'pattern' at the current position. If there's a match, the scanner
        advances the scan pointer and returns the matched string. Otherwise, the scanner
        returns None.'''
        match = self._get_match(pattern, flags)
        if match is not None:
            self._pos = match.end()
            return match.group(0)
        return None

    def check (self, pattern, flags = 0):
        '''Returns the value that 'scan' would return, without advancing the scan pointer'''
        match = self._get_match(pattern, flags)
        if match is not None: return match.group(0)
        return None

    def rest (self):
        '''returns the rest of the string (everything after the scan pointer)'''
        return self._string[self._pos:]

    def reset (self):
        '''reset the scan pointer to 0'''
        self._pos = 0

    def eos (self):
        '''return True if the scanner is at the end of the string'''
        return self._pos >= len(self._string)

    def line_number (self):
        '''returns the scanner's current line number'''
        # count the number of newlines up to _pos
        pattern = re.compile(r'\n')
        newlines = 0
        pos = 0
        while True:
            match = pattern.search(self._string, pos)
            if match is None or match.end() > self._pos:
                break
            newlines += 1
            pos = match.end()
        return newlines

    def _get_match (self, pattern, flags):
        if isinstance(pattern,basestring):
            pattern = re.compile(pattern, flags)
        return pattern.match(self._string, self._pos)

if __name__ == "__main__":
    scanner = StringScanner("   1\n2\n3")
    print(scanner.scan(r'\s+'))
    print(scanner.check(r'\s'))
    print(scanner._pos)