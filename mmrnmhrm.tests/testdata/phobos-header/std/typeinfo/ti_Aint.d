
module std.typeinfo.ti_Aint;

private import std.c.string;

// int[]

class TypeInfo_Ai : TypeInfo
{
    string toString() { return "int[]"; }

    hash_t getHash(in void *p)
    {	int[] s = *cast(int[]*)p;
	auto len = s.length;
	auto str = s.ptr;
	hash_t hash = 0;

	while (len)
	{
	    hash *= 9;
	    hash += *cast(uint *)str;
	    str++;
	    len--;
	}

	return hash;
    }

    int equals(in void *p1, in void *p2)
    {
	int[] s1 = *cast(int[]*)p1;
	int[] s2 = *cast(int[]*)p2;

	return s1.length == s2.length &&
	       memcmp(cast(void *)s1, cast(void *)s2, s1.length * int.sizeof) == 0;
    }

    int compare(in void *p1, in void *p2)
    {
	int[] s1 = *cast(int[]*)p1;
	int[] s2 = *cast(int[]*)p2;
	size_t len = s1.length;

	if (s2.length < len)
	    len = s2.length;
	for (size_t u = 0; u < len; u++)
	{
	    int result = s1[u] - s2[u];
	    if (result)
		return result;
	}
	return cast(int)s1.length - cast(int)s2.length;
    }

    size_t tsize()
    {
	return (int[]).sizeof;
    }

    uint flags()
    {
	return 1;
    }

    TypeInfo next()
    {
	return typeid(int);
    }
}

// uint[]

class TypeInfo_Ak : TypeInfo_Ai
{
    string toString() { return "uint[]"; }

    int compare(in void *p1, in void *p2)
    {
	uint[] s1 = *cast(uint[]*)p1;
	uint[] s2 = *cast(uint[]*)p2;
	size_t len = s1.length;

	if (s2.length < len)
	    len = s2.length;
	for (size_t u = 0; u < len; u++)
	{
	    int result = s1[u] - s2[u];
	    if (result)
		return result;
	}
	return cast(int)s1.length - cast(int)s2.length;
    }

    TypeInfo next()
    {
	return typeid(uint);
    }
}

// dchar[]

class TypeInfo_Aw : TypeInfo_Ak
{
    string toString() { return "dchar[]"; }

    TypeInfo next()
    {
	return typeid(dchar);
    }
}

